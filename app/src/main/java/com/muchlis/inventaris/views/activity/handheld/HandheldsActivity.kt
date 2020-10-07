package com.muchlis.inventaris.views.activity.handheld

import android.annotation.SuppressLint
import android.app.Dialog
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.dto.FindHandheldDto
import com.muchlis.inventaris.data.response.HandheldListResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityHandheldsBinding
import com.muchlis.inventaris.recycler_adapter.HandheldAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.handheld.HandheldsViewModel
import es.dmoral.toasty.Toasty

class HandheldsActivity : AppCompatActivity() {

    private lateinit var bd: ActivityHandheldsBinding
    private lateinit var viewModel: HandheldsViewModel

    private lateinit var optionJsonObject: SelectOptionResponse

    //recyclerview
    private lateinit var handheldAdapter: HandheldAdapter
    private var handheldData: MutableList<HandheldListResponse.Handheld> = mutableListOf()

    //first time animation
    private var isFirstTimeLoad = true

    //Dropdown dialog
    private lateinit var myDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityHandheldsBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(HandheldsViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        bd.refreshHandheldList.setOnRefreshListener {
            //jika direfresh akan mereset firsttime load supaya muncul animasi
            isFirstTimeLoad = true
            //menghapus text di pencarian
            bd.etHandheldlistSearchbar.setQuery("", false)
            findHandhelds()
        }

        //init handhelds
        findHandhelds()

        setSearchBarListener()

        bd.btHandheldlistTambah.setOnClickListener {
            intentToHandheldAppendActivity()
        }

        //INIT dialog
        myDialog = Dialog(this)

        bd.chipHandheldlistFilter.setOnClickListener {
            showFilterDialog()
        }

        //HIDE KEYBOARD
        bd.etHandheldlistSearchbar.isFocusable = false
        bd.etHandheldlistSearchbar.clearFocus()
    }

    private fun observeViewModel() {

        viewModel.run {
            getHandheldData().observe(this@HandheldsActivity, {
                loadRecyclerView(it)
            })
            isLoading.observe(this@HandheldsActivity, { showLoading(it) })
            messageError.observe(this@HandheldsActivity, { showErrorToast(it) })
        }
    }

    private fun findHandhelds(
        search: String = "",
        branch: String = App.prefs.userBranchSave,
        location: String = "",
        deactive: String = NO //no or yes
    ) {
        if (search.isEmpty()) {
            viewModel.findHandheldsFromServer(
                FindHandheldDto(
                    branch = branch,
                    handheldName = "",
                    deactive = deactive,
                    location = location,
                )
            )
        } else {
            //Not Valid IP Address mean search for name
            viewModel.findHandheldsFromServer(
                FindHandheldDto(
                    branch = App.prefs.userBranchSave,
                    handheldName = search,
                    deactive = deactive,
                    location = location,
                )
            )
        }
    }

    private fun setRecyclerView() {
        bd.rvHandheldlist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        handheldAdapter = HandheldAdapter(this, handheldData) {
            intentToHandheldDetailActivity(it.id)
        }
        bd.rvHandheldlist.adapter = handheldAdapter
        bd.rvHandheldlist.setHasFixedSize(true)
    }

    private fun setSearchBarListener() {
        bd.etHandheldlistSearchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    findHandhelds(search = query)
                }
                return false
            }

        })
    }

    private fun intentToHandheldDetailActivity(handheldID: String) {
//        val intent = Intent(this, HandheldDetailActivity::class.java)
//        intent.putExtra(INTENT_HH_TO_DETAIL, handheldID)
//        startActivity(intent)
    }

    private fun intentToHandheldAppendActivity() {
//        val intent = Intent(this, AppendHandheldActivity::class.java)
//        startActivity(intent)
    }

    private fun loadRecyclerView(data: HandheldListResponse) {
        handheldData.clear()
        handheldData.addAll(data.handhelds)
        if (isFirstTimeLoad) {
            //Jika aktifity dimuat pertama kali akan melakukan animasi
            runLayoutAnimation()
            isFirstTimeLoad = false
        }
        handheldAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.handhelds.count() == 0) {
            bd.ivEmptyList.visible()
        } else {
            bd.ivEmptyList.invisible()
        }

        setTotalHandheldCount(data.handhelds.count())
    }

    private fun runLayoutAnimation() {
        bd.rvHandheldlist.scheduleLayoutAnimation()
        bd.rvHandheldlist.invalidate()
    }

    @SuppressLint("SetTextI18n")
    private fun setTotalHandheldCount(total: Int) {
        bd.tvHandheldlistUnit.text = "Jumlah : $total unit"
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

        var locationDropdown: Spinner? = null

        //mengisi semua opsi untuk spinner
        val branchDropdownOption = optionJsonObject.kalimantan
        val locationDropdownOption: MutableList<String> = mutableListOf()
        val statusDropdownOption = listOf(AKTIF, NONAKTIF, SEMUA)

        //Mendapatkan index dari isian awal
        val branchIndexStart = branchDropdownOption.indexOf(App.prefs.userBranchSave)
        val statusIndexStart = 0

        //Nilai yang terpilih
        var branchSelected = ""
        var locationSelected = ""
        var statusSelected = ""

        //set layout untuk dialog
        myDialog.setContentView(R.layout.dialog_filter_handheld)

        //INISIASI TEXTINPUT LAYOUT
        val searchText: TextInputLayout = myDialog.findViewById(R.id.et_handheldfilter_searchbar)

        //INISIASI Spinner

        //BRANCH DROPDOWN
        val branchDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_handheld_branch)
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
                locationDropdown?.adapter =
                    ArrayAdapter<String>(
                        this@HandheldsActivity,
                        android.R.layout.simple_list_item_1,
                        locationDropdownOption
                    )
            }

        }

        //LOCATION DROPDOWN
        locationDropdown = myDialog.findViewById(R.id.sp_filter_handheld_lokasi)
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


        //STATUS DROPDOWN
        val statusDropdown: Spinner = myDialog.findViewById(R.id.sp_filter_handheld_status)
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
        val buttonHandheldDialogFilter: Button =
            myDialog.findViewById(R.id.bt_filter_handheld_apply)
        buttonHandheldDialogFilter.setOnClickListener {

            //VALIDASI
            if (locationSelected == SEMUA) {
                locationSelected = ""
            }
            statusSelected = when (statusSelected) {
                NONAKTIF -> YES
                AKTIF -> NO
                else -> ""
            }

            //CALL SERVER
            findHandhelds(
                search = searchText.editText?.text.toString(),
                branch = branchSelected,
                location = locationSelected,
                deactive = statusSelected
            )

            myDialog.dismiss()
        }

        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()
    }


    private fun showLoading(isLoading: Boolean) {
        bd.refreshHandheldList.isRefreshing = isLoading
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(this, text, Toasty.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.activityHHListMustBeRefresh) {
            findHandhelds()
            App.activityHHListMustBeRefresh = false
        }
    }
}