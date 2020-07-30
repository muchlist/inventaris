package com.muchlis.inventaris.views.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.databinding.FragmentComputerDetailBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.ComputerDetailViewModel
import com.muchlis.inventaris.views.activity.computer.EditComputerActivity
import com.muchlis.inventaris.views.activity.history.AppendHistoryActivity
import es.dmoral.toasty.Toasty


class ComputerDetailFragment : Fragment() {

    private var _binding: FragmentComputerDetailBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: ComputerDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComputerDetailBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ComputerDetailViewModel::class.java)
        observeViewModel()

        viewModel.getComputerFromServer()


        bd.ivDetailDelete.setOnClickListener {
            deleteComputerDetail()
        }

        bd.ivDetailAddHistory.setOnClickListener {
            intentToAppendHistoryActivity(viewModel.getComputerData().value)
        }

        bd.ivDetailDeactive.setOnClickListener {
            switchActiveComputerDetail()
        }

        bd.ivDetailEdit.setOnClickListener {
            viewModel.getComputerData().value?.let {
                intentToEditComputerActivity(it)
            }
        }

        bd.ivDetailLoad.setOnClickListener {
            viewModel.getComputerFromServer()
        }
    }

    private fun intentToAppendHistoryActivity(data: ComputerDetailResponse?) {
        val intent = Intent(requireActivity(), AppendHistoryActivity::class.java)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_ID, data?.id)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_CATEGORY, CATEGORY_PC)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_NAME, data?.clientName)
        startActivity(intent)
    }

    private fun intentToEditComputerActivity(data: ComputerDetailResponse){
            val intent = Intent(this.activity, EditComputerActivity::class.java)
            intent.putExtra(INTENT_TO_EDIT_COMPUTER, data)
            startActivity(intent)
    }

    private fun observeViewModel() {

        viewModel.run {
            getComputerData().observe(
                viewLifecycleOwner,
                Observer { setDataToDetailComputerView(it) })
            messageError.observe(viewLifecycleOwner, Observer { showToast(it, true) })
            messageDeleteComputerSuccess.observe(
                viewLifecycleOwner,
                Observer { showToast(it) })
            isdeleteComputerSuccess.observe(viewLifecycleOwner, Observer {
                computerDeletedKillActivity(it)
            })
        }
    }

    private fun setDataToDetailComputerView(data: ComputerDetailResponse) {
        bd.tvDetailHeader.text = data.clientName
        bd.tvDetailHostname.text = data.hostname
        bd.tvDetailIpAddress.text = data.ipAddress
        bd.tvDetailNoInventory.text = data.inventoryNumber
        bd.tvDetailBranch.text = data.branch
        bd.tvDetailDivision.text = data.division
        bd.tvDetailLocation.text = data.location

        val categoryMerkyear =
            "${data.tipe} - ${data.merk} - ${data.year.toDate().toStringJustYear()}"
        bd.tvDetailSubHeader.text = categoryMerkyear
        bd.tvDetailSeatManajemen.text = if (data.seatManagement) {
            "true"
        } else {
            "false"
        }
        bd.tvDetailOs.text = data.operationSystem
        bd.tvDetailProsessor.text = data.spec.processor.toString()
        bd.tvDetailRam.text = data.spec.ram.toString()
        bd.tvDetailHardisk.text = data.spec.hardisk.toString()
        bd.tvDetailStatus.text = data.lastStatus
        bd.tvDetailNote.text = data.note

        //Status deactive
        if (data.deactive){
            bd.tvDeactiveStatus.visible()
            bd.ivDetailDeactive.setImageResource(R.drawable.icons8_show)
        } else {
            bd.tvDeactiveStatus.invisible()
            bd.ivDetailDeactive.setImageResource(R.drawable.icons8_remove)
        }

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
        builder.setMessage("Yakin ingin menghapus komputer?")

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.deleteComputerFromServer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun switchActiveComputerDetail() {
        val builder = AlertDialog.Builder(requireActivity())

        val statusExisting = viewModel.getComputerData().value?.deactive ?: false

        builder.setTitle("Konfirmasi")

        if (statusExisting){
            builder.setMessage("Komputer akan diaktifkan")
        } else {
            builder.setMessage("Yakin ingin menonaktifkan komputer?\nKomputer ini akan hilang dari daftar komputer aktif!")
        }

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.changeComputerStatusFromServer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun computerDeletedKillActivity(isDeleted: Boolean){
        if (isDeleted){
            requireActivity().finish()
            App.activityComputerListMustBeRefresh = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.fragmentDetailComputerMustBeRefresh) {
            viewModel.getComputerFromServer()
            App.fragmentDetailComputerMustBeRefresh = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}