package com.muchlis.inventaris.views.activity.stock

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.dto.FindStocksDto
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.data.response.StockListResponse
import com.muchlis.inventaris.databinding.ActivityStocksBinding
import com.muchlis.inventaris.recycler_adapter.StockAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.stock.StocksViewModel
import es.dmoral.toasty.Toasty

class StocksActivity : AppCompatActivity() {

    private lateinit var bd: ActivityStocksBinding
    private lateinit var viewModel: StocksViewModel

    private lateinit var optionJsonObject: SelectOptionResponse

    //recyclerview
    private lateinit var stockAdapter: StockAdapter
    private var stockData: MutableList<StockListResponse.Stock> = mutableListOf()

    //first time animation
    private var isFirstTimeLoad = true

    //Dropdown dialog
    private lateinit var myDialog: Dialog

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

        //init stock
        findStocks()

        setSearchBarListener()

        bd.btListTambah.setOnClickListener {
            intentToStockAppendActivity()
        }

        //INIT dialog
        myDialog = Dialog(this)

        bd.chipFilter.setOnClickListener {
            showFilterDialog()
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

    private fun findStocks(
        search: String = "",
        branch: String = App.prefs.userBranchSave,
        category: String = "",
        location: String = "",
        deactive: String = NO
    ) {
        if (search.isEmpty()) {
            viewModel.findStockFromServer(
                FindStocksDto(
                    branch = branch,
                    stockName = "",
                    deactive = deactive,
                    category = category,
                    location = location
                )
            )
        } else {
            viewModel.findStockFromServer(
                FindStocksDto(
                    branch = branch,
                    stockName = search,
                    deactive = deactive,
                    category = category,
                    location = location
                )
            )
        }
    }

    private fun intentToStockAppendActivity() {
        val intent = Intent(this, AppendStockActivity::class.java)
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

    private fun validateJsonStringInSharedPrefsForDropdown() {
        if (JsonMarshaller().getOption() != null) {
            optionJsonObject = JsonMarshaller().getOption()!!
        } else {
            Toasty.error(this, ERR_DROPDOWN_NOT_LOAD, Toasty.LENGTH_LONG).show()
        }
    }

    private fun showFilterDialog() {

        //Memvalidasi json option yang disimpan di sharepref
        validateJsonStringInSharedPrefsForDropdown()

        lateinit var locationDropdown: Spinner

        //mengisi semua opsi untuk spinner
        val branchDropdownOption = optionJsonObject.kalimantan
        val locationDropdownOption: MutableList<String> = mutableListOf()

        val categoryDropdownOption: MutableList<String> = mutableListOf()
        categoryDropdownOption.addAll(optionJsonObject.stockCategory)
        categoryDropdownOption.add(0, SEMUA)

        val statusDropdownOption = listOf(AKTIF, NONAKTIF, SEMUA)

        //Mendapatkan index dari isian awal
        val branchIndexStart = branchDropdownOption.indexOf(App.prefs.userBranchSave)
        val statusIndexStart = 0
        val categoryIndexStart = 0

        //Nilai yang terpilih
        var branchSelected = ""
        var locationSelected = ""
        var categorySelected = ""
        var statusSelected = ""

        //set layout untuk dialog
        myDialog.setContentView(R.layout.dialog_filter_stock)

        //INISIASI TEXTINPUT LAYOUT
        val searchText: TextInputLayout = myDialog.findViewById(R.id.et_stockfilter_searchbar)

        //INISIASI Spinner

        //BRANCH DROPDOWN
        val branchDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_stock_branch)
        branchDropdown.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, branchDropdownOption)

        branchDropdown.setSelection(branchIndexStart)
        branchDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                branchSelected = branchDropdownOption[position]
                locationDropdownOption.clear()
                val locationFiltered = optionJsonObject.locations.filter { s ->
                    s.contains(branchSelected) || s.contains(LAINNYA)
                }
                locationDropdownOption.addAll(locationFiltered)
                locationDropdownOption.add(0, SEMUA)
                locationDropdown.adapter =
                    ArrayAdapter<String>(
                        this@StocksActivity,
                        android.R.layout.simple_list_item_1,
                        locationDropdownOption
                    )
            }

        }

        //LOCATION DROPDOWN
        locationDropdown = myDialog.findViewById(R.id.sp_filter_stock_lokasi)
        locationDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                locationSelected = locationDropdownOption[position]
            }


        }

        //CATEGORY DROPDOWN
        val categoryDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_stock_category)
        categoryDropdown.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryDropdownOption)

        categoryDropdown.setSelection(categoryIndexStart)
        categoryDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                categorySelected = categoryDropdownOption[position]
            }
        }


        //STATUS DROPDOWN
        val statusDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_stock_status)
        statusDropdown.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statusDropdownOption)

        statusDropdown.setSelection(statusIndexStart)
        statusDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                statusSelected = statusDropdownOption[position]
            }
        }

        //TOMBOL APPLY
        val buttonComputerDialogFilter: Button =
            myDialog.findViewById(R.id.bt_filter_stock_apply)
        buttonComputerDialogFilter.setOnClickListener {

            //VALIDASI
            if (categorySelected == SEMUA) {
                categorySelected = ""
            }
            if (locationSelected == SEMUA) {
                locationSelected = ""
            }
            statusSelected = when (statusSelected) {
                NONAKTIF -> YES
                AKTIF -> NO
                else -> ""
            }

            //CALL SERVER
            findStocks(
                search = searchText.editText?.text.toString(),
                branch = branchSelected,
                location = locationSelected,
                category = categorySelected,
                deactive = statusSelected
            )

            myDialog.dismiss()
        }

        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()
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
        if (App.activityStockListMustBeRefresh) {
            findStocks()
            App.activityStockListMustBeRefresh = false
        }
    }
}

