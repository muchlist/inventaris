package com.muchlis.inventaris.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.databinding.FragmentComputerHistoryBinding
import com.muchlis.inventaris.recycler_adapter.HistoryAdapter
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.views.view_model.ComputerDetailViewModel
import es.dmoral.toasty.Toasty

class ComputerHistoryFragment : Fragment() {

    private var _binding: FragmentComputerHistoryBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: ComputerDetailViewModel

    //Inisiasi data on resume just one time
    private var displayFirstTime = false

    //recyclerview
    private lateinit var historyAdapter: HistoryAdapter
    private var historyData: MutableList<HistoryResponse> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComputerHistoryBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ComputerDetailViewModel::class.java)

        setRecyclerView()

        bd.refreshDetailComputerHistory.setOnRefreshListener {
            viewModel.findHistories()
        }

        observeViewModel()

    }

    private fun observeViewModel() {

        viewModel.run {
            getHistoryData().observe(viewLifecycleOwner, Observer { loadRecyclerView(it) })
            messageError.observe(viewLifecycleOwner, Observer { showErrorToast(it) })
            messageSuccess.observe(viewLifecycleOwner, Observer { showSuccessToast(it) })
            isLoading.observe(viewLifecycleOwner, Observer { showLoading(it) })
        }
    }

    private fun setRecyclerView() {
        bd.rvDetailComputerHistory.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter(requireActivity(), historyData) {
            //TODO Click
        }
        bd.rvDetailComputerHistory.adapter = historyAdapter
        bd.rvDetailComputerHistory.setHasFixedSize(true)
    }

    private fun loadRecyclerView(data: HistoryListResponse) {
        historyData.clear()
        historyData.addAll(data.histories)
        bd.rvDetailComputerHistory.scheduleLayoutAnimation()
        bd.rvDetailComputerHistory.invalidate()
        historyAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
//        if (it.containers.count() == 0){
//            view?.iv_level_one_empty?.visible()
//        } else {
//            view?.iv_level_one_empty?.invisible()
//        }
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

    private fun showLoading(isLoading: Boolean) {
        bd.refreshDetailComputerHistory.isRefreshing = isLoading
    }

    override fun onResume() {
        super.onResume()
        if (!displayFirstTime) {
            viewModel.findHistories()
            displayFirstTime = true
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}