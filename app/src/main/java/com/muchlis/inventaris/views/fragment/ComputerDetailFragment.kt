package com.muchlis.inventaris.views.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.databinding.FragmentComputerDetailBinding
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.toDate
import com.muchlis.inventaris.utils.toStringJustYear
import com.muchlis.inventaris.view_model.ComputerDetailViewModel
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

//        val options = OptionsMarshaller().getOption()
//        if (options == null){
//            Toasty.error(requireActivity(),"Error Saat Mendapatkan Option",Toasty.LENGTH_LONG).show()
//        }
//
//        val locations = options!!.locations
//
//        var locationString = ""
//        for (location in locations){
//            locationString = locationString + "$location\n"
//        }

        viewModel.getComputer()


        bd.ivDetailDelete.setOnClickListener {
            deleteComputerDetail()
        }
    }

    private fun observeViewModel() {

        viewModel.run {
            getComputerData().observe(viewLifecycleOwner, Observer { setDataToDetailComputerView(it) })
            messageError.observe(viewLifecycleOwner, Observer { showErrorToast(it) })
            messageSuccess.observe(viewLifecycleOwner, Observer { showSuccessToast(it) })
        }
    }


    private fun setDataToDetailComputerView(data : ComputerDetailResponse){
        bd.tvDetailClientName.text = data.clientName
        bd.tvDetailHostname.text = data.hostname
        bd.tvDetailIpAddress.text = data.ipAddress
        bd.tvDetailNoInventory.text = data.inventoryNumber
        bd.tvDetailBranch.text = data.branch
        bd.tvDetailDivision.text = data.division
        bd.tvDetailLocation.text = data.location

        val categoryMerkyear = "${data.tipe} - ${data.merk} - ${data.year.toDate().toStringJustYear()}"
        bd.tvDetailFullPc.text = categoryMerkyear
        bd.tvDetailSeatManajemen.text = if (data.seatManagement) {"true"} else {"false"}
        bd.tvDetailOs.text = data.operationSystem
        bd.tvDetailProsessor.text = data.spec.processor.toString()
        bd.tvDetailRam.text = data.spec.ram.toString()
        bd.tvDetailHardisk.text = data.spec.hardisk.toString()
        bd.tvDetailStatus.text = data.lastStatus
        bd.tvDetailNote.text = data.note
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(requireActivity(), text, Toasty.LENGTH_LONG).show()
        }
    }

    private fun showSuccessToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.success(requireActivity(), text, Toasty.LENGTH_LONG).show()
        }
    }

    private fun deleteComputerDetail() {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Mengahapus Komputer")
        builder.setMessage("Konfirmasi untuk menghapus komputer, komputer tidak dapat dihapus 2 jam setelah pembuatan!")

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.deleteComputer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}