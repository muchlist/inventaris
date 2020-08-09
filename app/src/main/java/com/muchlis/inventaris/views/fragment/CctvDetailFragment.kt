package com.muchlis.inventaris.views.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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

class CctvDetailFragment : Fragment() {

    private var _binding: FragmentCctvDetailBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: CctvDetailViewModel

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
        bd.tvDetailStatus.text = data.lastStatus
        bd.tvDetailNote.text = data.note

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

    }

    private fun setLineChart(data: List<CctvDetailResponse.PingState>) {
        val dataReverse = data.reversed()

        val pingState = mutableListOf<Entry>()
        var index = 0
        while (index < dataReverse.count()) {
            pingState.add(Entry(index.toFloat(), dataReverse[index].code.toFloat()))
            index++
        }

        val lineDataSet = LineDataSet(pingState, "0 = Down, 2 = Up")

        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawFilled(true)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//        lineDataSet.cubicIntensity = 0.5f

        //SET DESCRIPTION for Chart with last ping date and begin ping date
        val beginPing = dataReverse[0].timeDate.toDate().toStringDateForView()
        val lastPing = dataReverse[dataReverse.lastIndex].timeDate.toDate().toStringDateForView()
        val description = Description()
        description.text = "$beginPing    sd    $lastPing"
        bd.lineChart.description = description

        bd.lineChart.xAxis.isEnabled = false
        bd.lineChart.xAxis.valueFormatter
        bd.lineChart.axisRight.isEnabled = false
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
    }
}