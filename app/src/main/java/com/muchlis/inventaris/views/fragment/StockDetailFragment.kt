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
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.databinding.FragmentStockDetailBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.StockDetailViewModel
import com.muchlis.inventaris.views.activity.computer.EditComputerActivity
import com.muchlis.inventaris.views.activity.history.AppendHistoryActivity
import es.dmoral.toasty.Toasty

class StockDetailFragment : Fragment() {

    private var _binding: FragmentStockDetailBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: StockDetailViewModel


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
//            viewModel.getComputerData().value?.let {
//                intentToEditComputerActivity(it)
//            }
            //TODO
        }
    }

    private fun intentToAppendHistoryActivity(data: ComputerDetailResponse?) {
        val intent = Intent(requireActivity(), AppendHistoryActivity::class.java)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_ID, data?.id)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_CATEGORY, CATEGORY_PC)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_NAME, data?.clientName)
        startActivity(intent)
    }

    private fun intentToEditComputerActivity(data: ComputerDetailResponse) {
        val intent = Intent(this.activity, EditComputerActivity::class.java)
        intent.putExtra(INTENT_TO_EDIT_COMPUTER, data)
        startActivity(intent)
    }

    private fun observeViewModel() {

        viewModel.run {
            getStockData().observe(viewLifecycleOwner, Observer { setDataToDetailStockView(it) })
            messageError.observe(viewLifecycleOwner, Observer { showToast(it, true) })
            isdeleteStockSuccess.observe(
                viewLifecycleOwner,
                Observer { stockDeletedKillActivity(it) })
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