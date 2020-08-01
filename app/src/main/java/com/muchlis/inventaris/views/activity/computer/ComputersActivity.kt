package com.muchlis.inventaris.views.activity.computer

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
import com.muchlis.inventaris.data.dto.FindComputersDto
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityComputerBinding
import com.muchlis.inventaris.recycler_adapter.ComputerAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.ComputersViewModel
import es.dmoral.toasty.Toasty

class ComputersActivity : AppCompatActivity() {

    private lateinit var bd: ActivityComputerBinding
    private lateinit var viewModel: ComputersViewModel

    private lateinit var optionJsonObject: SelectOptionResponse

    //recyclerview
    private lateinit var computerAdapter: ComputerAdapter
    private var computerData: MutableList<ComputerListResponse.Computer> = mutableListOf()

    //first time animation
    private var isFirstTimeLoad = true

    //Dropdown dialog
    private lateinit var myDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityComputerBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(ComputersViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        bd.refreshComputerList.setOnRefreshListener {
            //jika direfresh akan mereset firsttime load supaya muncul animasi
            isFirstTimeLoad = true
            //menghapus text di pencarian
            bd.etComputerlistSearchbar.setQuery("", false)
            findComputers()
        }

        //init computers
        findComputers()

        setSearchBarListener()

        bd.btComputerlistTambah.setOnClickListener {
            intentToComputerAppendActivity()
        }

        //INIT dialog
        myDialog = Dialog(this)

        bd.chipComputerlistFilter.setOnClickListener {
            showFilterDialog()
        }

        //HIDE KEYBOARD
        bd.etComputerlistSearchbar.isFocusable = false
        bd.etComputerlistSearchbar.clearFocus()
    }

    private fun observeViewModel() {

        viewModel.run {
            getComputerData().observe(this@ComputersActivity, Observer {
                loadRecyclerView(it)
            })
            isLoading.observe(this@ComputersActivity, Observer { showLoading(it) })
            messageError.observe(this@ComputersActivity, Observer { showErrorToast(it) })
        }
    }

    private fun findComputers(search: String = "") {
        if (search.isEmpty()) {
            viewModel.findComputersFromServer(
                FindComputersDto(
                    branch = App.prefs.userBranchSave,
                    ipAddress = "",
                    clientName = "",
                    deactive = ""
                )
            )
        } else {
            if (Validation().isIPAddressValid(search)) {
                //Valid IP Address
                viewModel.findComputersFromServer(
                    FindComputersDto(
                        branch = App.prefs.userBranchSave,
                        ipAddress = search,
                        clientName = "",
                        deactive = ""
                    )
                )
            } else {
                //Not Valid IP Address mean search for name
                viewModel.findComputersFromServer(
                    FindComputersDto(
                        branch = App.prefs.userBranchSave,
                        ipAddress = "",
                        clientName = search,
                        deactive = ""
                    )
                )
            }
        }
    }

    private fun setRecyclerView() {
        bd.rvComputerlist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        computerAdapter = ComputerAdapter(this, computerData) {
            intentToComputerDetailActivity(it.id)
        }
        bd.rvComputerlist.adapter = computerAdapter
        bd.rvComputerlist.setHasFixedSize(true)
    }

    private fun setSearchBarListener() {
        bd.etComputerlistSearchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    findComputers(search = query)
                }
                return false
            }

        })
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

    private fun loadRecyclerView(data: ComputerListResponse) {
        computerData.clear()
        computerData.addAll(data.computers)
        if (isFirstTimeLoad) {
            //Jika aktifity dimuat pertama kali akan melakukan animasi
            runLayoutAnimation()
            isFirstTimeLoad = false
        }
        computerAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.computers.count() == 0) {
            bd.ivEmptyList.visible()
        } else {
            bd.ivEmptyList.invisible()
        }

        setTotalComputerCount(data.computers.count())
    }

    private fun runLayoutAnimation() {
        bd.rvComputerlist.scheduleLayoutAnimation()
        bd.rvComputerlist.invalidate()
    }

    private fun setTotalComputerCount(total: Int) {
        bd.tvComputerlistUnit.text = "Jumlah : $total unit"
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
        val branchDropdownOption = listOf("BANJARMASIN", "BAGENDANG")//TODO sediakan cabang di json
        val locationDropdownOption: MutableList<String> = mutableListOf()

        val divisionDropdownOption: MutableList<String> = mutableListOf()
        divisionDropdownOption.addAll(optionJsonObject.divisions)
        divisionDropdownOption.add(0, "SEMUA")
        val seatManajemenDropdownOption = listOf("SEMUA", "YA", "TIDAK")
        val statusDropdownOption = listOf("AKTIF", "NONAKTIF", "SEMUA")

        //Mendapatkan index dari isian awal
        val branchIndexStart = branchDropdownOption.indexOf(App.prefs.userBranchSave)
        val locationIndexStart = 0
        val divisionIndexStart = 0
        val seatIndexStart = 0
        val statusIndexStart = 0

        //Nilai yang terpilih
        var branchSelected = ""
        var locationSelected = ""
        var divisionSelected = ""
        var seatSelected = ""
        var statusSelected = ""

        //set layout untuk dialog
        myDialog.setContentView(R.layout.dialog_filter_computer)

        //INISIASI TEXTINPUT LAYOUT
        val searchText: TextInputLayout = myDialog.findViewById(R.id.et_computerfilter_searchbar)

        //INISIASI Spinner

        //BRANCH DROPDOWN
        val branchDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_computer_branch)
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
                    s.contains(branchSelected) || s.contains("LAINNYA")
                }
                locationDropdownOption.addAll(locationFiltered)
                locationDropdownOption.add(0, "SEMUA")
                locationDropdown.adapter =
                    ArrayAdapter<String>(
                        this@ComputersActivity,
                        android.R.layout.simple_list_item_1,
                        locationDropdownOption
                    )
            }

        }

        //LOCATION DROPDOWN
        locationDropdown = myDialog.findViewById(R.id.sp_filter_computer_lokasi)
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

        //DIVISION DROPDOWN
        val divisionDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_computer_divisi)
        divisionDropdown.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, divisionDropdownOption)

        divisionDropdown.setSelection(divisionIndexStart)
        divisionDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                divisionSelected = divisionDropdownOption[position]
            }
        }

        //SEAT DROPDOWN
        val seatDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_computer_seat)
        seatDropdown.adapter =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                seatManajemenDropdownOption
            )

        seatDropdown.setSelection(seatIndexStart)
        seatDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                seatSelected = seatManajemenDropdownOption[position]
            }
        }

        //STATUS DROPDOWN
        val statusDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_computer_status)
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
            myDialog.findViewById(R.id.bt_filter_computer_apply)
        buttonComputerDialogFilter.setOnClickListener {

            //VALIDASI


            //CALL SERVER


            //TOAST
            showErrorToast("${searchText.editText?.text ?: ""} $branchSelected \n$divisionSelected \n$locationSelected \n$seatSelected \n$statusSelected")

            myDialog.dismiss()
        }

        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()
    }


    private fun showLoading(isLoading: Boolean) {
        bd.refreshComputerList.isRefreshing = isLoading
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(this, text, Toasty.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.activityComputerListMustBeRefresh) {
            findComputers()
            App.activityComputerListMustBeRefresh = false
        }
    }
}