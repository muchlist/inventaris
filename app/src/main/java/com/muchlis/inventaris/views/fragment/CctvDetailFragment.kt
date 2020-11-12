package com.muchlis.inventaris.views.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.CctvDetailResponse
import com.muchlis.inventaris.databinding.FragmentCctvDetailBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.cctv.CctvDetailViewModel
import com.muchlis.inventaris.views.activity.cctv.EditCctvActivity
import com.muchlis.inventaris.views.activity.history.AppendHistoryActivity
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CctvDetailFragment : Fragment() {

    private var _binding: FragmentCctvDetailBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: CctvDetailViewModel

    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE_GALERY = 1001
        private const val IMAGE_CAPTURE_CODE = 1008
        private const val PERMISSION_CODE_BACK = 20 //1002
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCctvDetailBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CctvDetailViewModel::class.java)
        observeViewModel()

        viewModel.getCctvFromServer()


        bd.ivDetailDelete.setOnClickListener {
            deleteComputerDetail()
        }

        bd.ivDetailAddHistory.setOnClickListener {
            intentToAppendHistoryActivity(viewModel.getCctvData().value)
        }

        bd.ivDetailDeactive.setOnClickListener {
            switchActiveComputerDetail()
        }

        bd.ivDetailEdit.setOnClickListener {
            viewModel.getCctvData().value?.let {
                intentToEditCctvActivity(it)
            }
        }

        bd.ivDetailLoad.setOnClickListener {
            viewModel.getCctvFromServer()
        }

        bd.ivDetailBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        bd.ivDetailImage.setOnLongClickListener {
            //permissionThenIntentBackCamera()
            permissionThenIntentGallery()
            false
        }

        bd.ivDetailImage.setOnClickListener {
            showToast("Tahan satu detik untuk mengupload gambar.")
        }

        loadImageIcon()
    }

    private fun loadImageIcon() {
        Glide
            .with(this)
            .load(R.drawable.icons8_remove)
            .into(bd.ivDetailDeactive)

        Glide
            .with(this)
            .load(R.drawable.addproperty64)
            .into(bd.ivDetailAddHistory)

        Glide
            .with(this)
            .load(R.drawable.editproperty64)
            .into(bd.ivDetailEdit)

        Glide
            .with(this)
            .load(R.drawable.deletedocument64)
            .into(bd.ivDetailDelete)
    }

    private fun intentToAppendHistoryActivity(data: CctvDetailResponse?) {
        val intent = Intent(requireActivity(), AppendHistoryActivity::class.java)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_ID, data?.id)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_CATEGORY, CATEGORY_CCTV)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_NAME, data?.cctvName)
        startActivity(intent)
    }

    private fun intentToEditCctvActivity(data: CctvDetailResponse) {
        val intent = Intent(this.activity, EditCctvActivity::class.java)
        intent.putExtra(INTENT_TO_EDIT_CCTV, data)
        startActivity(intent)
    }

    private fun observeViewModel() {

        viewModel.run {
            getCctvData().observe(
                viewLifecycleOwner,
                Observer {
                    setDataToDetailCctvView(it)
                    setLineChart(it.pingState)
                })
            messageError.observe(viewLifecycleOwner, Observer { showToast(it, true) })
            messageDeleteComputerSuccess.observe(
                viewLifecycleOwner,
                Observer { showToast(it) })
            isdeleteComputerSuccess.observe(viewLifecycleOwner, Observer {
                cctvDeletedKillActivity(it)
            })
            isLoading.observe(viewLifecycleOwner, Observer {
                showLoading(it)
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToDetailCctvView(data: CctvDetailResponse) {
        bd.tvDetailHeader.text = data.cctvName
        bd.tvDetailIpAddress.text = data.ipAddress
        bd.tvDetailNoInventory.text = data.inventoryNumber
        bd.tvDetailBranch.text = data.branch
        bd.tvDetailLocation.text = data.location
        bd.tvTahun.text = data.year.toDate().toStringJustYear()
        bd.tvMerkTipe.text = data.tipe + " " + data.merk
        bd.tvLastPing.text = data.lastPing
        bd.tvDetailNote.text = data.note

        //Last Status
        var lastStatus = ""
        if (data.case.count() != 0){
            for (case in data.case){
                lastStatus = lastStatus + "* " + case.caseNote + "\n"
            }
        } else {
            lastStatus = "Nihil"
        }
        bd.tvDetailStatus.text = lastStatus

        //Status deactive
        if (data.deactive) {
            bd.tvDeactiveStatus.visible()
            Glide
                .with(this)
                .load(R.drawable.icons8_show)
                .into(bd.ivDetailDeactive)
        } else {
            bd.tvDeactiveStatus.invisible()
            Glide
                .with(this)
                .load(R.drawable.icons8_remove)
                .into(bd.ivDetailDeactive)
        }

        //image
        if (data.image.isNotEmpty()) {
            val imageUrl = App.prefs.baseUrl + "/static/images/" + data.image
            Glide
                .with(this)
                .load(imageUrl)
                .into(bd.ivDetailImage)
        } else {
            Glide
                .with(this)
                .load(R.drawable.ic_undraw_folder_x4ft)
                .into(bd.ivDetailImage)
        }

    }

    private fun setLineChart(data: List<CctvDetailResponse.PingState>) {
        val dataReverse = data.reversed()

        val pingState = mutableListOf<Entry>()
        var index = 0
        while (index < dataReverse.count()) {
            pingState.add(Entry(index.toFloat(), dataReverse[index].code.toFloat()))
            index++
        }

        val lineDataSet = LineDataSet(pingState, "2 = Up, 0 = Down")

        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawFilled(true)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//        lineDataSet.cubicIntensity = 0.5f

        //SET DESCRIPTION for Chart with last ping date and begin ping date
        val beginPing = dataReverse[0].timeDate.toDate().toStringDateForView()
        val lastPing = dataReverse[dataReverse.lastIndex].timeDate.toDate().toStringDateForView()
        val description = Description()
        description.text = "$beginPing    sd    $lastPing  WIB"
        bd.lineChart.description = description

        bd.lineChart.xAxis.isEnabled = false
        bd.lineChart.xAxis.valueFormatter
        bd.lineChart.axisRight.isEnabled = false
        bd.lineChart.axisLeft.axisMaximum = 2.5f
        bd.lineChart.axisLeft.axisMinimum = -0.5f
        bd.lineChart.axisLeft.isEnabled = false
        bd.lineChart.animateY(500)

        val dataLine = LineData(lineDataSet)
        bd.lineChart.data = dataLine

        //refresh
        bd.lineChart.invalidate()
    }

    private fun showToast(text: String, isError: Boolean = false) {
        if (text.isNotEmpty()) {
            if (isError) {
                Toasty.error(requireActivity(), text, Toasty.LENGTH_LONG).show()
            } else {
                Toasty.success(requireActivity(), text, Toasty.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteComputerDetail() {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Konfirmasi")
        builder.setMessage("Yakin ingin menghapus cctv?")

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.deleteCctvFromServer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun switchActiveComputerDetail() {
        val builder = AlertDialog.Builder(requireActivity())

        val statusExisting = viewModel.getCctvData().value?.deactive ?: false

        builder.setTitle("Konfirmasi")

        if (statusExisting) {
            builder.setMessage("Komputer akan diaktifkan")
        } else {
            builder.setMessage("Yakin ingin menonaktifkan cctv?\nCctv ini akan hilang dari daftar cctv aktif!")
        }

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.changeCctvStatusFromServer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


//    private fun permissionThenIntentBackCamera() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//            if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
//                requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
//            ) {
//                //Permission was not enabled
//                val permission = arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//                //show pop up request permission
//                requestPermissions(permission, PERMISSION_CODE_BACK)
//            } else {
//                //permission already granted
//                intentToCameraActivity()
//            }
//        } else {
//            //system os < Marshmallow
//            intentToCameraActivity()
//        }
//    }

    private fun permissionThenIntentGallery() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
            ) {
                //Permission was not enabled
                val permission = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                //show pop up request permission
                requestPermissions(permission, PERMISSION_CODE_GALERY)
            } else {
                //permission already granted
                pickImageFromGalery()
            }
        } else {
            //system os < Marshmallow
            pickImageFromGalery()
        }
    }

    private fun pickImageFromGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun intentToCameraActivity() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 0)
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 0)
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", false)
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        startActivityForResult(intent, IMAGE_CAPTURE_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //Called when user allow or deny permission
        when (requestCode) {
            PERMISSION_CODE_BACK -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission popup was granted
                    intentToCameraActivity()
                } else {
                    //permission popup was denny
                    showToast("Penggunaan kamera tidak diijinkan", false)
                }
            }
            PERMISSION_CODE_GALERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGalery()
                } else {
                    showToast("Penggunaan galery tidak diijinkan", false)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
//            IMAGE_CAPTURE_CODE -> {
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    val bitmapPhoto = data.extras?.get("data") as Bitmap
//                    val filePhoto = createTempFile(bitmapPhoto)
//                    val reqFile = RequestBody.create(MediaType.parse("image/*"), filePhoto)
//                    viewModel.uploadImageFromServer(
//                        imageFile = reqFile
//                    )
//                }
//            }
            IMAGE_PICK_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val imageUri = data.data

                    imageUri?.let {

                        val inputStream =
                            requireActivity().contentResolver.openInputStream(it)

                        val file = File(
                            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "${System.currentTimeMillis()} _image.jpg"
                        )

                        try {
                            FileOutputStream(file).use { outputStream ->
                                inputStream?.copyTo(outputStream)
                            }
                        } catch (e: FileNotFoundException) {
                            showToast("File tidak ditemukan", true)
                        } catch (e: IOException) {
                            showToast("IOException", true)
                        }

                        scope.launch {
                            val fileCompressed = compressImage(file)
                            val reqFile =
                                RequestBody.create(MediaType.parse("image/*"), fileCompressed)
                            viewModel.uploadImageFromServer(
                                imageFile = reqFile
                            )
                        }
                    }
                }
            }
            else -> {
                showToast("Request code tidak dikenali", true)
            }
        }
    }


    private suspend fun compressImage(file: File): File {
        return Compressor.compress(requireActivity(), file)
    }

    /*fun createTempFile(bitmap: Bitmap): File {
        val file = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${System.currentTimeMillis()} _image.jpg"
        )
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val bitmapdata = baos.toByteArray()

        try {
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }*/


    private fun cctvDeletedKillActivity(isDeleted: Boolean) {
        if (isDeleted) {
            requireActivity().finish()
            App.activityCctvListMustBeRefresh = true
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bd.loading.visible()
        } else {
            bd.loading.invisible()
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.fragmentDetailCctvMustBeRefresh) {
            viewModel.getCctvFromServer()
            App.fragmentDetailCctvMustBeRefresh = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scope.cancel()
    }
}