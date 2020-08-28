package com.muchlis.inventaris.views.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.databinding.FragmentStockDetailBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.stock.StockDetailViewModel
import com.muchlis.inventaris.views.activity.stock.EditStockActivity
import com.muchlis.inventaris.views.activity.stock.StockUseActivity
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class StockDetailFragment : Fragment() {

    private var _binding: FragmentStockDetailBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: StockDetailViewModel

    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val INCREMENT_STOCK_CODE = 400
        private val PERMISSION_CODE_GALERY = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockDetailBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(StockDetailViewModel::class.java)
        observeViewModel()

        viewModel.getStockFromServer()

        bd.ivDetailDelete.setOnClickListener {
            deleteStockDetail()
        }

        bd.ivDetailDeactive.setOnClickListener {
            switchActiveStockDetail()
        }

        bd.ivDetailEdit.setOnClickListener {
            viewModel.getStockData().value?.let {
                intentToEditStockActivity(it)
            }
        }

        bd.ivDetailLoad.setOnClickListener {
            viewModel.getStockFromServer()
        }

        bd.ivDetailIncrement.setOnClickListener {
            intentToStockUseActivity(viewModel.getStockData().value, INCREMENT_MODE)
        }

        bd.ivDetailDecrement.setOnClickListener {
            intentToStockUseActivity(viewModel.getStockData().value, DECREMENT_MODE)
        }

        bd.ivDetailBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        bd.ivDetailImage.setOnLongClickListener {
            permissionThenIntentGallery()
            false
        }

        bd.ivDetailImage.setOnClickListener {
            showToast("Tahan satu detik untuk mengupload gambar.")
        }
    }

    private fun intentToStockUseActivity(data: StockDetailResponse?, mode: String) {
        data?.let {
            val intent = Intent(requireActivity(), StockUseActivity::class.java)
            intent.putExtra(INTENT_TO_STOCK_USE_CREATE_ID, it.id)
            intent.putExtra(INTENT_TO_STOCK_USE_MODE, mode)
            intent.putExtra(INTENT_TO_STOCK_USE_NAME, it.stockName)
            startActivityForResult(intent, INCREMENT_STOCK_CODE)
        }
    }

    private fun intentToEditStockActivity(data: StockDetailResponse) {
        val intent = Intent(this.activity, EditStockActivity::class.java)
        intent.putExtra(INTENT_TO_EDIT_STOCK, data)
        startActivity(intent)
    }

    private fun observeViewModel() {

        viewModel.run {
            getStockData().observe(viewLifecycleOwner, Observer { setDataToDetailStockView(it) })
            messageError.observe(viewLifecycleOwner, Observer { showToast(it, true) })
            isdeleteStockSuccess.observe(
                viewLifecycleOwner,
                Observer { stockDeletedKillActivity(it) })
            isLoading.observe(viewLifecycleOwner, Observer {
                showLoading(it)
            })
        }
    }

    private fun setDataToDetailStockView(data: StockDetailResponse) {
        bd.tvDetailHeader.text = data.stockName
        bd.tvDetailBranch.text = data.branch
        bd.tvDetailLocation.text = data.location
        bd.tvDetailCategory.text = data.category
        bd.tvDetailSubHeader.text = "Sisa ${data.qty.toStringView()} ${data.unit}"

        bd.tvIncrement.text = viewModel.getTotalIncrement() + " " + data.unit
        bd.tvDecrement.text = viewModel.getTotalDecrement() + " " + data.unit

        bd.tvDetailUpdate.text = data.updatedAt.toDate().toStringDateForView() + " WIB"

        bd.tvDetailNote.text = data.note

        //Status deactive
        if (data.deactive) {
            bd.tvDeactiveStatus.visible()
            bd.ivDetailDeactive.setImageResource(R.drawable.icons8_show)
        } else {
            bd.tvDeactiveStatus.invisible()
            bd.ivDetailDeactive.setImageResource(R.drawable.icons8_remove)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //Called when user allow or deny permission
        when (requestCode) {
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
            INCREMENT_STOCK_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val dataResult: StockDetailResponse? =
                        data?.getParcelableExtra(INTENT_RESULT_STOCK)
                    dataResult?.let { viewModel.setStockData(it) }
                }
            }
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


    suspend fun compressImage(file: File): File {
        return Compressor.compress(requireActivity(), file)
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

    private fun deleteStockDetail() {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Konfirmasi")
        builder.setMessage("Yakin ingin menghapus stok ini?")

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.deleteStockFromServer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun switchActiveStockDetail() {
        val builder = AlertDialog.Builder(requireActivity())

        val statusExisting = viewModel.getStockData().value?.deactive ?: false

        builder.setTitle("Konfirmasi")

        if (statusExisting) {
            builder.setMessage("Stok akan diaktifkan")
        } else {
            builder.setMessage("Yakin ingin menonaktifkan stok?\nStok ini akan hilang dari daftar stok aktif!")
        }

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.changeStockStatusFromServer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun stockDeletedKillActivity(isDeleted: Boolean) {
        if (isDeleted) {
            requireActivity().finish()
            App.activityStockListMustBeRefresh = true
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
        if (App.fragmentDetailStockMustBeRefresh) {
            viewModel.getStockFromServer()
            App.fragmentDetailStockMustBeRefresh = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}