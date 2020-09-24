package com.muchlis.inventaris.views.activity.pelindo_apps_history

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.dto.FindAppHistoryDto
import com.muchlis.inventaris.data.response.HistoryAppsListResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityPelindoAppsHistoryListBinding
import com.muchlis.inventaris.recycler_adapter.AppHistoryAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.pelindo_app_history.PelindoAppsHistoryViewModel
import es.dmoral.toasty.Toasty

class PelindoAppsHistoryListActivity : AppCompatActivity() {

    private lateinit var bd: ActivityPelindoAppsHistoryListBinding
    private lateinit var viewModel: PelindoAppsHistoryViewModel

    private lateinit var optionJsonObject: SelectOptionResponse

    //recyclerview
    private lateinit var appHistoryAdapter: AppHistoryAdapter
    private var historyData: MutableList<HistoryAppsListResponse.History> = mutableListOf()

    //first time animation
    private var isFirstTimeLoad = true

    //Dropdown dialog
    private lateinit var myDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityPelindoAppsHistoryListBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(PelindoAppsHistoryViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        bd.refreshDetailAppHistory.setOnRefreshListener {
            //jika direfresh akan mereset firsttime load supaya muncul animasi
            isFirstTimeLoad = true
            findHistory()
        }

        findHistory()
        viewModel.findApps()

        //INIT dialog
        myDialog = Dialog(this)

        bd.fabPelindoAppHistory.setOnClickListener {
            startActivity(Intent(this, AppendPelindoAppsHistoryActivity::class.java))
        }

        bd.chipFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun findHistory(
        appName: String = "",
        branch: String = "",
        status: String = ""
    ) {
        viewModel.findHistories(
            FindAppHistoryDto(
                appName = appName,
                branch = branch,
                status = status,
                limit = 100
            )
        )
    }

    private fun observeViewModel() {

        viewModel.run {

            getHistoryData().observe(this@PelindoAppsHistoryListActivity, {
                loadRecyclerView(it)
            })

            getCount.observe(this@PelindoAppsHistoryListActivity,{
                bd.tvAppHistoryTotalCount.text = it
            })
            getMinute.observe(this@PelindoAppsHistoryListActivity,{
                bd.tvAppHistoryTotalMinute.text = it
            })

            isLoading.observe(this@PelindoAppsHistoryListActivity, { showLoading(it) })

            messageError.observe(
                this@PelindoAppsHistoryListActivity,
                { showToast(it, true) })

            messageSuccess.observe(
                this@PelindoAppsHistoryListActivity,
                { showToast(it, false) })

            isHistoryDeleted.observe(this@PelindoAppsHistoryListActivity, {
                findHistory()
            })
        }
    }

    private fun setRecyclerView() {
        bd.rvDetailAppHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val onClickItem: (HistoryAppsListResponse.History) -> Unit = {
            if (!it.isComplete && it.branch == App.prefs.userBranchSave) {
                val intent = Intent(this, EditPelindoAppsHistoryActivity::class.java)
                intent.putExtra(INTENT_PELINDO_HISTORY_TO_EDIT, it)
                startActivity(intent)
            }
        }

        val onLongClickItem: (HistoryAppsListResponse.History) -> Unit = {
            if (it.branch == App.prefs.userBranchSave) {
                deleteHistory(it.id)
            }
        }

        appHistoryAdapter = AppHistoryAdapter(
            this,
            historyData,
            onClickItem,
            onLongClickItem
        )
        bd.rvDetailAppHistory.adapter = appHistoryAdapter
        bd.rvDetailAppHistory.setHasFixedSize(true)
    }

    private fun loadRecyclerView(data: HistoryAppsListResponse) {
        historyData.clear()
        historyData.addAll(data.histories)
        if (isFirstTimeLoad) {
            //Jika aktifity dimuat pertama kali akan melakukan animasi
            runLayoutAnimation()
            isFirstTimeLoad = false
        }
        appHistoryAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.histories.count() == 0) {
            bd.ivEmptyList.visible()
        } else {
            bd.ivEmptyList.invisible()
        }
    }

    private fun runLayoutAnimation() {
        bd.rvDetailAppHistory.scheduleLayoutAnimation()
        bd.rvDetailAppHistory.invalidate()
    }


    private fun showFilterDialog() {

        //Memvalidasi json option yang disimpan di sharepref dan diload di server
        validateJsonStringInSharedPrefsForDropdownAndAppListNameExist()

        //mengisi semua opsi untuk spinner
        val branchDropdownOption: MutableList<String> = mutableListOf()
        branchDropdownOption.add(0, SEMUA)
        branchDropdownOption.addAll(optionJsonObject.kalimantan)

        val appNameDropdownOption: MutableList<String> = mutableListOf()
        appNameDropdownOption.add(0, SEMUA)
        appNameDropdownOption.addAll(viewModel.getAppsListName())

        val appStatusDropdownOption: MutableList<String> = mutableListOf()
        appStatusDropdownOption.add(0, SEMUA)
        appStatusDropdownOption.addAll(optionJsonObject.appHistory)

        //Mendapatkan index dari isian awal
        val branchIndexStart = 0
        val appNameIndexStart = 0
        val appStatusIndexStart = 0

        //Nilai yang terpilih
        var branchSelected = ""
        var appNameSelected = ""
        var appStatusSelected = ""

        //set layout untuk dialog
        myDialog.setContentView(R.layout.dialog_filter_app_history)

        //INISIASI Spinner
        //APLICATION DROPDOWN
        val applicationDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_history_application)
        applicationDropdown.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appNameDropdownOption)

        applicationDropdown.setSelection(appNameIndexStart)
        applicationDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                appNameSelected = appNameDropdownOption[position]
            }

        }

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

        //STATUS DROPDOWN
        val statusDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_history_status)
        statusDropdown.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appStatusDropdownOption)

        statusDropdown.setSelection(appStatusIndexStart)
        statusDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                appStatusSelected = appStatusDropdownOption[position]
            }

        }


        //TOMBOL APPLY
        val buttonComputerDialogFilter: Button =
            myDialog.findViewById(R.id.bt_filter_history_apply)
        buttonComputerDialogFilter.setOnClickListener {

            //VALIDASI
            if (appNameSelected == SEMUA) {
                appNameSelected = ""
            }
            if (branchSelected == SEMUA) {
                branchSelected = ""
            }
            if (appStatusSelected == SEMUA) {
                appStatusSelected = ""
            }

            //CALL SERVER
            viewModel.findHistories(
                FindAppHistoryDto(
                    appName = appNameSelected,
                    branch = branchSelected,
                    status = appStatusSelected,
                    limit = 100
                )
            )

            myDialog.dismiss()
        }

        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()
    }

    private fun validateJsonStringInSharedPrefsForDropdownAndAppListNameExist() {
        if (JsonMarshaller().getOption() != null) {
            optionJsonObject = JsonMarshaller().getOption()!!
        } else {
            Toasty.error(this, ERR_DROPDOWN_NOT_LOAD, Toasty.LENGTH_LONG).show()
        }
    }

    private fun deleteHistory(historyID: String) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Konfirmasi")
        builder.setMessage("Yakin ingin menghapus insiden?")

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
        bd.refreshDetailAppHistory.isRefreshing = isLoading
    }

    private fun showToast(text: String, isError: Boolean = false) {
        if (text.isNotEmpty()) {
            if (isError) {
                Toasty.error(this, text, Toasty.LENGTH_LONG).show()
            } else {
                Toasty.success(this, text, Toasty.LENGTH_LONG).show()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (App.activityAppsHistoryListMustBeRefresh) {
            findHistory()
            App.activityAppsHistoryListMustBeRefresh = false
        }
    }


}