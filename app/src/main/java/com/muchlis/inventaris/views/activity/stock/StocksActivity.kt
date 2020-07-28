package com.muchlis.inventaris.views.activity.stock

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.dto.FindStocksDto
import com.muchlis.inventaris.data.response.StockListResponse
import com.muchlis.inventaris.databinding.ActivityStocksBinding
import com.muchlis.inventaris.recycler_adapter.StockAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.StocksViewModel
import com.muchlis.inventaris.views.activity.computer.AppendComputerActivity
import com.muchlis.inventaris.views.activity.computer.ComputerDetailActivity
import es.dmoral.toasty.Toasty

class StocksActivity : AppCompatActivity() {

    private lateinit var bd: ActivityStocksBinding
    private lateinit var viewModel: StocksViewModel

    //recyclerview
    private lateinit var stockAdapter: StockAdapter
    private var stockData: MutableList<StockListResponse.Stock> = mutableListOf()

    //first time animation
    private var isFirstTimeLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityStocksBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(StocksViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        bd.refreshList.setOnRefreshListener {
            //jika direfresh akan mereset firsttime load supaya muncul animasi
            isFirstTimeLoad = true
            //menghapus text di pencarian
            bd.etListSearchbar.setQuery("", false)
            findStocks()
        }

        //init computers
        findStocks()

        setSearchBarListener()

        bd.btListTambah.setOnClickListener {
            intentToComputerAppendActivity()
        }

        //HIDE KEYBOARD
        bd.etListSearchbar.isFocusable = false
        bd.etListSearchbar.clearFocus()

    }

    private fun observeViewModel() {

        viewModel.run {
            getStockData().observe(this@StocksActivity, Observer {
                loadRecyclerView(it)
            })
            isLoading.observe(this@StocksActivity, Observer { showLoading(it) })
            messageError.observe(this@StocksActivity, Observer { showErrorToast(it) })
        }
    }

    private fun setRecyclerView() {
        bd.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        stockAdapter = StockAdapter(this, stockData) {
            intentToStockDetailActivity(it.id)
        }
        bd.rvList.adapter = stockAdapter
        bd.rvList.setHasFixedSize(true)
    }

    private fun intentToStockDetailActivity(stockID: String) {
        val intent = Intent(this, StockDetailActivity::class.java)
        intent.putExtra(INTENT_STOCK_TO_DETAIL, stockID)
        startActivity(intent)
    }

    private fun setSearchBarListener() {
        bd.etListSearchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    findStocks(search = query)
                }
                return false
            }

        })
    }

    private fun findStocks(search: String = "") {
        if (search.isEmpty()) {
            viewModel.findStockFromServer(
                FindStocksDto(
                    branch = App.prefs.userBranchSave,
                    stockName = "",
                    deactive = ""
                )
            )
        } else {
            viewModel.findStockFromServer(
                FindStocksDto(
                    branch = App.prefs.userBranchSave,
                    stockName = search,
                    deactive = ""
                )
            )
        }
    }

    private fun intentToComputerDetailActivity(computerID: String) {
        val intent = Intent(this, ComputerDetailActivity::class.java)
        intent.putExtra(INTENT_PC_TO_DETAIL, computerID)
        startActivity(intent)
    }

    private fun intentToComputerAppendActivity() {
        val intent = Intent(this, AppendComputerActivity::class.java)
        startActivity(intent)
    }

    private fun loadRecyclerView(data: StockListResponse) {
        stockData.clear()
        stockData.addAll(data.stocks)
        if (isFirstTimeLoad) {
            //Jika aktifity dimuat pertama kali akan melakukan animasi
            runLayoutAnimation()
            isFirstTimeLoad = false
        }
        stockAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.stocks.count() == 0) {
            bd.ivEmptyList.visible()
        } else {
            bd.ivEmptyList.invisible()
        }
    }

    private fun runLayoutAnimation() {
        bd.rvList.scheduleLayoutAnimation()
        bd.rvList.invalidate()
    }

    private fun showLoading(isLoading: Boolean) {
        bd.refreshList.isRefreshing = isLoading
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(this, text, Toasty.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.activityComputerListMustBeRefresh) {
            findStocks()
            App.activityComputerListMustBeRefresh = false
        }
    }
}

