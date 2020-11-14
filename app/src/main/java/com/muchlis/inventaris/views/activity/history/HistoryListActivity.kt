package com.muchlis.inventaris.views.activity.history

import android.app.AlertDialog
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.dto.FindHistoryDto
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityHistoryListBinding
import com.muchlis.inventaris.recycler_adapter.HistoryAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.history.HistoryListViewModel
import com.muchlis.inventaris.views.activity.cctv.CctvDetailActivity
import com.muchlis.inventaris.views.activity.computer.ComputerDetailActivity
import com.muchlis.inventaris.views.activity.handheld.HandheldDetailActivity
import com.muchlis.inventaris.views.activity.stock.StockDetailActivity
import es.dmoral.toasty.Toasty

class HistoryListActivity : AppCompatActivity() {

    private lateinit var bd: ActivityHistoryListBinding
    private lateinit var viewModel: HistoryListViewModel

    private lateinit var optionJsonObject: SelectOptionResponse

    //recyclerview
    private lateinit var historyAdapter: HistoryAdapter
    private var historyData: MutableList<HistoryResponse> = mutableListOf()

    //Dropdown dialog
    private lateinit var myDialog: Dialog

    //first time animation
    private var isFirstTimeLoad = true

    private var isComplete = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityHistoryListBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        //MENERIMA DATA DARI intent dan mengirim ke viewModel
        isComplete = intent.getIntExtra(
            INTENT_TO_HISTORY_LIST, 100
        )

        viewModel = ViewModelProvider(this).get(HistoryListViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        findHistories()

        //INIT dialog
        myDialog = Dialog(this)

        bd.chipFilter.setOnClickListener {
            showFilterDialog()
        }

        bd.refreshDetailHistory.setOnRefreshListener {
            findHistories()
        }
    }

    private fun findHistories() {
        viewModel.findHistories(
            FindHistoryDto(
                branch = "",
                category = "",
                limit = 100,
                isComplete = isComplete,
            )
        )
    }

    private fun observeViewModel() {

        viewModel.run {
            getHistoryData().observe(this@HistoryListActivity, Observer {
                loadRecyclerView(it)
            })
            isLoading.observe(this@HistoryListActivity, Observer { showLoading(it) })
            messageError.observe(this@HistoryListActivity, Observer { showErrorToast(it) })
            isHistoryDeleted.observe(this@HistoryListActivity, Observer {
                if (it) {
                    findHistories()
                }
            })
        }
    }

    private fun setRecyclerView() {
        bd.rvDetailHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val onClickItem: (HistoryResponse) -> Unit = {
            if (!it.isComplete && it.branch == App.prefs.userBranchSave) {
                val intent = Intent(this, EditHistoryActivity::class.java)
                intent.putExtra(INTENT_TO_HISTORY_EDIT, it)
                startActivity(intent)
            }
        }

        val onLongClickItem: (HistoryResponse) -> Unit = {
            if (it.category == CATEGORY_DAILY) {
                //Khusus Daily perilakunya delete daily
                if (it.author == App.prefs.nameSave) {
                    deleteDailyHistory(it.id)
                }
            } else {
                intentToDetailActivity(parentID = it.parentId, category = it.category)
            }
        }

        historyAdapter = HistoryAdapter(this, historyData, onClickItem, onLongClickItem)
        bd.rvDetailHistory.adapter = historyAdapter
        bd.rvDetailHistory.setHasFixedSize(true)
    }

    private fun loadRecyclerView(data: HistoryListResponse) {
        historyData.clear()
        historyData.addAll(data.histories)
        if (isFirstTimeLoad) {
            //Jika aktifity dimuat pertama kali akan melakukan animasi
            runLayoutAnimation()
            isFirstTimeLoad = false
        }
        historyAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.histories.count() == 0) {
            bd.ivEmptyList.visible()
        } else {
            bd.ivEmptyList.invisible()
        }
    }

    private fun runLayoutAnimation() {
        bd.rvDetailHistory.scheduleLayoutAnimation()
        bd.rvDetailHistory.invalidate()
    }

    private fun intentToDetailActivity(parentID: String, category: String) {
        when (category) {
            CATEGORY_PC -> {
                intentToComputerDetailActivity(parentID)
            }
            CATEGORY_STOCK -> {
                intentToStockDetailActivity(parentID)
            }
            CATEGORY_CCTV -> {
                intentToCctvDetailActivity(parentID)
            }
            CATEGORY_TABLET -> {
                intentToHandheldDetailActivity(parentID)
            }
            else -> {
                Toasty.error(this, "Category tidak valid", Toasty.LENGTH_LONG).show()
            }
        }
    }

    private fun intentToComputerDetailActivity(unitID: String) {
        val intent = Intent(this, ComputerDetailActivity::class.java)
        intent.putExtra(INTENT_PC_TO_DETAIL, unitID)
        startActivity(intent)
    }

    private fun intentToCctvDetailActivity(unitID: String) {
        val intent = Intent(this, CctvDetailActivity::class.java)
        intent.putExtra(INTENT_CCTV_TO_DETAIL, unitID)
        startActivity(intent)
    }

    private fun intentToStockDetailActivity(unitID: String) {
        val intent = Intent(this, StockDetailActivity::class.java)
        intent.putExtra(INTENT_STOCK_TO_DETAIL, unitID)
        startActivity(intent)
    }

    private fun intentToHandheldDetailActivity(unitID: String) {
        val intent = Intent(this, HandheldDetailActivity::class.java)
        intent.putExtra(INTENT_HH_TO_DETAIL, unitID)
        startActivity(intent)
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

        //mengisi semua opsi untuk spinner
        val branchDropdownOption: MutableList<String> = mutableListOf()
        branchDropdownOption.addAll(optionJsonObject.kalimantan)
        branchDropdownOption.add(0, SEMUA)

        val categoryDropdownOption = listOf(
            SEMUA,
            CATEGORY_PC,
            CATEGORY_CCTV,
            CATEGORY_STOCK,
            CATEGORY_TABLET,
            CATEGORY_APPLICATION,
            CATEGORY_DAILY
        )

        //Mendapatkan index dari isian awal
        val branchIndexStart = 0
        val categoryIndexStart = 0

        //Nilai yang terpilih
        var branchSelected = ""
        var categorySelected = ""

        //set layout untuk dialog
        myDialog.setContentView(R.layout.dialog_filter_history)

        //INISIASI Spinner
        //BRANCH DROPDOWN
        val branchDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_history_branch)
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
            }

        }

        //CATEGORY DROPDOWN
        val categoryDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_history_category)
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


        //TOMBOL APPLY
        val buttonComputerDialogFilter: Button =
            myDialog.findViewById(R.id.bt_filter_history_apply)
        buttonComputerDialogFilter.setOnClickListener {

            //VALIDASI
            if (categorySelected == SEMUA) {
                categorySelected = ""
            }
            if (branchSelected == SEMUA) {
                branchSelected = ""
            }

            //CALL SERVER
            viewModel.findHistories(
                FindHistoryDto(
                    branch = branchSelected,
                    category = categorySelected,
                    limit = 100,
                    isComplete = isComplete
                )
            )

            myDialog.dismiss()
        }

        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()
    }

    private fun deleteDailyHistory(historyID: String) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Konfirmasi")
        builder.setMessage("Yakin ingin menghapus daily?")

        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.deleteHistoryFromServer(historyID)
        }
        builder.setNeutralButton("Batal") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showLoading(isLoading: Boolean) {
        bd.refreshDetailHistory.isRefreshing = isLoading
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(this, text, Toasty.LENGTH_LONG).show()
        }
    }
}