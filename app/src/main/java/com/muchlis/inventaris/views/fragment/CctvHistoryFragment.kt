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
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.response.CctvDetailResponse
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.databinding.FragmentListHistoryBinding
import com.muchlis.inventaris.recycler_adapter.HistoryAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.cctv.CctvDetailViewModel
import com.muchlis.inventaris.views.activity.history.AppendHistoryActivity
import es.dmoral.toasty.Toasty

class CctvHistoryFragment : Fragment() {

    private var _binding: FragmentListHistoryBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: CctvDetailViewModel

    //Inisiasi data on resume just one time
    private var displayFirstTime = false

    //recyclerview
    private lateinit var historyAdapter: HistoryAdapter
    private var historyData: MutableList<HistoryResponse> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListHistoryBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CctvDetailViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        bd.refreshDetailComputerHistory.setOnRefreshListener {
            viewModel.findHistoriesFromServer()
        }

        bd.fabDetailComputerHistory.setOnClickListener {
            intentToAppendHistoryActivity(viewModel.getCctvData().value)
        }

    }

    private fun observeViewModel() {

        viewModel.run {
            getHistoryData().observe(viewLifecycleOwner, Observer { loadRecyclerView(it) })
            messageHistoryError.observe(viewLifecycleOwner, Observer { showToast(it, true) })
            messageDeleteHistorySuccess.observe(
                viewLifecycleOwner,
                Observer { showToast(it, false) })
            isDeleteHistorySuccess.observe(
                viewLifecycleOwner,
                Observer { viewModel.findHistoriesFromServer() })
            isLoading.observe(viewLifecycleOwner, Observer { showLoading(it) })
        }
    }

    private fun setRecyclerView() {
        bd.rvDetailComputerHistory.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter(requireActivity(), historyData) {
            if (it.author == App.prefs.nameSave){
                deleteComputerHistory(it.id)
            }
        }
        bd.rvDetailComputerHistory.adapter = historyAdapter
        bd.rvDetailComputerHistory.setHasFixedSize(true)
    }

    private fun deleteComputerHistory(historyID: String) {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Konfirmasi")
        builder.setMessage("Yakin ingin menghapus riwayat?")

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.deleteHistoryFromServer(historyID)
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun intentToAppendHistoryActivity(data: CctvDetailResponse?) {
        val intent = Intent(requireActivity(), AppendHistoryActivity::class.java)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_ID, data?.id)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_CATEGORY, CATEGORY_CCTV)
        intent.putExtra(INTENT_TO_HISTORY_CREATE_NAME, data?.cctvName)
        startActivity(intent)
    }

    private fun loadRecyclerView(data: HistoryListResponse) {
        historyData.clear()
        historyData.addAll(data.histories)
        bd.rvDetailComputerHistory.scheduleLayoutAnimation()
        bd.rvDetailComputerHistory.invalidate()
        historyAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.histories.count() == 0) {
            bd.ivEmptyList.visible()
        } else {
            bd.ivEmptyList.invisible()
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


    private fun showLoading(isLoading: Boolean) {
        bd.refreshDetailComputerHistory.isRefreshing = isLoading
    }

    override fun onResume() {
        super.onResume()
        if (!displayFirstTime) {

            //Memanggil history hanya pada saat viewpager pertama dibuka
            viewModel.findHistoriesFromServer()
            displayFirstTime = true

        } else {

            //reload history apabila App.fragmentHistoryComputerMustBeRefresh == true
            if (App.fragmentHistoryComputerMustBeRefresh) {
                viewModel.findHistoriesFromServer()
                App.fragmentHistoryComputerMustBeRefresh = false
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}