package com.muchlis.inventaris.views.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.HandheldDetailResponse
import com.muchlis.inventaris.databinding.FragmentHandheldDetailBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.handheld.HandheldDetailViewModel
import com.muchlis.inventaris.views.activity.handheld.EditHandheldActivity
import com.muchlis.inventaris.views.activity.history.AppendHistoryActivity
import es.dmoral.toasty.Toasty

class HandheldDetailFragment : Fragment() {

    private var _binding: FragmentHandheldDetailBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: HandheldDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHandheldDetailBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(HandheldDetailViewModel::class.java)
        observeViewModel()

        viewModel.getHandheldFromServer()

        loadImageIcon()

        bd.ivDetailDelete.setOnClickListener {
            deleteHandheldDetail()
        }

        bd.ivDetailAddHistory.setOnClickListener {
            intentToAppendHistoryActivity(viewModel.getHandheldData().value)
        }

        bd.ivDetailDeactive.setOnClickListener {
            switchActiveHandheldDetail()
        }

        bd.ivDetailEdit.setOnClickListener {
            viewModel.getHandheldData().value?.let {
                intentToEditHandheldActivity(it)
            }
        }

        bd.ivDetailLoad.setOnClickListener {
            viewModel.getHandheldFromServer()
        }

        bd.ivDetailBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
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

    private fun intentToAppendHistoryActivity(data: HandheldDetailResponse?) {
        val intent = Intent(requireActivity(), AppendHistoryActivity::class.java)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_ID, data?.id)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_CATEGORY, CATEGORY_TABLET)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_NAME, data?.handheldName)
        startActivity(intent)
    }

    private fun intentToEditHandheldActivity(data: HandheldDetailResponse) {
        val intent = Intent(this.activity, EditHandheldActivity::class.java)
        intent.putExtra(INTENT_TO_EDIT_HH, data)
        startActivity(intent)
    }

    private fun observeViewModel() {

        viewModel.run {
            getHandheldData().observe(
                viewLifecycleOwner,
                { setDataToDetailHandheldView(it) })
            messageError.observe(viewLifecycleOwner, { showToast(it, true) })
            messageDeleteHandheldSuccess.observe(
                viewLifecycleOwner,
                { showToast(it) })
            isdeleteHandheldSuccess.observe(viewLifecycleOwner, {
                handheldDeletedKillActivity(it)
            })
            isLoading.observe(viewLifecycleOwner, {
                showLoading(it)
            })
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bd.loading.visible()
        } else {
            bd.loading.invisible()
        }
    }

    private fun setDataToDetailHandheldView(data: HandheldDetailResponse) {
        bd.tvDetailHeader.text = data.handheldName
        bd.tvDetailNoHp.text = data.phone
        bd.tvDetailIpAddress.text = data.ipAddress
        bd.tvDetailNoInventory.text = data.inventoryNumber
        bd.tvDetailBranch.text = data.branch
        bd.tvDetailLocation.text = data.location

        val categoryMerkyear =
            "${data.tipe} - ${data.merk} - ${data.year.toDate().toStringJustYear()}"
        bd.tvDetailSubHeader.text = categoryMerkyear
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

    private fun deleteHandheldDetail() {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Konfirmasi")
        builder.setMessage("Yakin ingin menghapus tablet?")

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.deleteHandheldFromServer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun switchActiveHandheldDetail() {
        val builder = AlertDialog.Builder(requireActivity())

        val statusExisting = viewModel.getHandheldData().value?.deactive ?: false

        builder.setTitle("Konfirmasi")

        if (statusExisting) {
            builder.setMessage("Tablet akan diaktifkan")
        } else {
            builder.setMessage("Yakin ingin menonaktifkan tablet?\nTablet ini akan hilang dari daftar tablet aktif!")
        }

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.changeHandheldStatusFromServer()
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun handheldDeletedKillActivity(isDeleted: Boolean) {
        if (isDeleted) {
            requireActivity().finish()
            App.activityHHListMustBeRefresh = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.fragmentDetailHHMustBeRefresh) {
            viewModel.getHandheldFromServer()
            App.fragmentDetailHHMustBeRefresh = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}