package com.muchlis.inventaris.views.activity.cctv

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
import com.muchlis.inventaris.data.dto.FindCctvDto
import com.muchlis.inventaris.data.response.CctvListResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityCctvsBinding
import com.muchlis.inventaris.recycler_adapter.CctvAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.cctv.CctvsViewModel
import es.dmoral.toasty.Toasty

class CctvsActivity : AppCompatActivity() {

    private lateinit var bd: ActivityCctvsBinding
    private lateinit var viewModel: CctvsViewModel

    private lateinit var optionJsonObject: SelectOptionResponse

    //recyclerview
    private lateinit var cctvAdapter: CctvAdapter
    private var cctvData: MutableList<CctvListResponse.Cctv> = mutableListOf()

    //first time animation
    private var isFirstTimeLoad = true

    //Dropdown dialog
    private lateinit var myDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityCctvsBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(CctvsViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        bd.refreshList.setOnRefreshListener {
            //jika direfresh akan mereset firsttime load supaya muncul animasi
            isFirstTimeLoad = true
            //menghapus text di pencarian
            bd.etListSearchbar.setQuery("", false)
            findCctvs()
        }

        //init stock
        findCctvs()

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
            getCctvData().observe(this@CctvsActivity, Observer {
                loadRecyclerView(it)
            })
            isLoading.observe(this@CctvsActivity, Observer { showLoading(it) })
            messageError.observe(this@CctvsActivity, Observer { showErrorToast(it) })
        }
    }

    private fun setRecyclerView() {
        bd.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cctvAdapter = CctvAdapter(this, cctvData) {
            intentToCctvDetailActivity(it.id)
        }
        bd.rvList.adapter = cctvAdapter
        bd.rvList.setHasFixedSize(true)
    }

    private fun intentToCctvDetailActivity(stockID: String) {
        val intent = Intent(this, CctvDetailActivity::class.java)
        intent.putExtra(INTENT_CCTV_TO_DETAIL, stockID)
        startActivity(intent)
    }

    private fun setSearchBarListener() {
        bd.etListSearchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    findCctvs(search = query)
                }
                return false
            }

        })
    }

    private fun findCctvs(
        search: String = "",
        branch: String = App.prefs.userBranchSave,
        deactive: String = NO,
        location: String = "",
        pingState: String = ""
    ) {
        if (search.isEmpty()) {
            viewModel.findCctvFromServer(
                FindCctvDto(
                    branch = branch,
                    cctvName = "",
                    deactive = deactive,
                    ipAddress = "",
                    lastPing = pingState,
                    location = location
                )
            )
        } else {
            if (Validation().isIPAddressValid(search)) {
                //Valid IP Address
                viewModel.findCctvFromServer(
                    FindCctvDto(
                        branch = branch,
                        cctvName = "",
                        deactive = deactive,
                        ipAddress = search,
                        lastPing = pingState,
                        location = location
                    )
                )
            } else {
                //Not valid IP Address means search for name
                viewModel.findCctvFromServer(
                    FindCctvDto(
                        branch = branch,
                        cctvName = search,
                        deactive = deactive,
                        ipAddress = "",
                        lastPing = pingState,
                        location = location
                    )
                )
            }
        }
    }

    private fun intentToStockAppendActivity() {
        val intent = Intent(this, AppendCctvActivity::class.java)
        startActivity(intent)
    }

    private fun loadRecyclerView(data: CctvListResponse) {
        cctvData.clear()
        cctvData.addAll(data.cctvs)
        if (isFirstTimeLoad) {
            //Jika aktifity dimuat pertama kali akan melakukan animasi
            runLayoutAnimation()
            isFirstTimeLoad = false
        }
        cctvAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.cctvs.count() == 0) {
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

        val pingDropdownOption = listOf(SEMUA, UP, DOWN)
        val statusDropdownOption = listOf(AKTIF, NONAKTIF, SEMUA)

        //Mendapatkan index dari isian awal
        val branchIndexStart = branchDropdownOption.indexOf(App.prefs.userBranchSave)
        val statusIndexStart = 0
        val pingIndexStart = 0

        //Nilai yang terpilih
        var branchSelected = ""
        var locationSelected = ""
        var pingSelected = ""
        var statusSelected = ""

        //set layout untuk dialog
        myDialog.setContentView(R.layout.dialog_filter_cctv)

        //INISIASI TEXTINPUT LAYOUT
        val searchText: TextInputLayout = myDialog.findViewById(R.id.et_cctvfilter_searchbar)

        //INISIASI Spinner

        //BRANCH DROPDOWN
        val branchDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_cctv_branch)
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
                        this@CctvsActivity,
                        android.R.layout.simple_list_item_1,
                        locationDropdownOption
                    )
            }

        }

        //LOCATION DROPDOWN
        locationDropdown = myDialog.findViewById(R.id.sp_filter_cctv_lokasi)
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

        //SEAT DROPDOWN
        val seatDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_cctv_ping)
        seatDropdown.adapter =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                pingDropdownOption
            )

        seatDropdown.setSelection(pingIndexStart)
        seatDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pingSelected = pingDropdownOption[position]
            }
        }

        //STATUS DROPDOWN
        val statusDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_cctv_status)
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
        val buttonCctvDialogFilter: Button =
            myDialog.findViewById(R.id.bt_filter_cctv_apply)
        buttonCctvDialogFilter.setOnClickListener {

            //VALIDASI
            if (locationSelected == SEMUA) {
                locationSelected = ""
            }
            statusSelected = when (statusSelected) {
                NONAKTIF -> YES
                AKTIF -> NO
                else -> ""
            }
            pingSelected = when (pingSelected) {
                UP -> UP
                DOWN -> DOWN
                else -> ""
            }

            //CALL SERVER
            findCctvs(
                search = searchText.editText?.text.toString(),
                branch = branchSelected,
                location = locationSelected,
                pingState = pingSelected,
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
        if (App.activityCctvListMustBeRefresh) {
            findCctvs()
            App.activityCctvListMustBeRefresh = false
        }
    }
}

