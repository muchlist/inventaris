package com.muchlis.inventaris.views.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.databinding.FragmentStockDecrementBinding
import com.muchlis.inventaris.recycler_adapter.StockIncrementDecrementAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.StockDetailViewModel
import com.muchlis.inventaris.views.activity.stock.StockUseActivity

class StockDecrementFragment : Fragment() {

    private var _binding: FragmentStockDecrementBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: StockDetailViewModel

    //recyclerview
    private lateinit var stockAdapter: StockIncrementDecrementAdapter
    private var stockData: MutableList<StockDetailResponse.IncrementDecrement> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockDecrementBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(StockDetailViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        bd.refreshDetailStockDecrement.setOnRefreshListener {
            viewModel.getStockFromServer()
        }

        bd.fabDetailStockDecrement.setOnClickListener {
            viewModel.getStockData().value?.let {
                intentToStockUseActivity(it)
            }
        }

    }

    private fun observeViewModel() {

        viewModel.run {
            getStockData().observe(viewLifecycleOwner, Observer { loadRecyclerView(it) })
            isLoading.observe(viewLifecycleOwner, Observer { showLoading(it) })
        }
    }

    private fun setRecyclerView() {
        stockAdapter = StockIncrementDecrementAdapter(requireActivity(), stockData) {
        }
        bd.rvDetailStockDecrement.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = stockAdapter
            setHasFixedSize(true)
        }
    }

    private fun intentToStockUseActivity(data: StockDetailResponse) {
        val intent = Intent(requireActivity(), StockUseActivity::class.java)
        intent.putExtra(INTENT_TO_STOCK_USE_CREATE_ID, data.id)
        intent.putExtra(INTENT_TO_STOCK_USE_MODE, DECREMENT_MODE)
        intent.putExtra(INTENT_TO_STOCK_USE_NAME, data.stockName)
        startActivityForResult(intent, 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 500) {
            if (resultCode == Activity.RESULT_OK) {
                val dataResult: StockDetailResponse? = data?.getParcelableExtra(INTENT_RESULT_STOCK)
                dataResult?.let { viewModel.setStockData(it) }
            }
        }
    }

    private fun loadRecyclerView(data: StockDetailResponse) {
        stockData.clear()
        stockData.addAll(data.decrement.reversed())
        bd.rvDetailStockDecrement.scheduleLayoutAnimation()
        bd.rvDetailStockDecrement.invalidate()
        stockAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.decrement.count() == 0) {
            bd.ivEmptyList.visible()
        } else {
            bd.ivEmptyList.invisible()
        }

    }


    private fun showLoading(isLoading: Boolean) {
        bd.refreshDetailStockDecrement.isRefreshing = isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}