package com.muchlis.inventaris.views.activity.cctv

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.dto.FindCctvDto
import com.muchlis.inventaris.data.response.CctvListResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityCctvsBinding
import com.muchlis.inventaris.recycler_adapter.CctvAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.cctv.CctvsViewModel
import com.muchlis.inventaris.views.activity.stock.AppendStockActivity
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
    //private lateinit var myDialog: Dialog

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
        //myDialog = Dialog(this)


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
        deactive: String = NO
    ) {
        if (search.isEmpty()) {
            viewModel.findCctvFromServer(
                FindCctvDto(
                    branch = branch,
                    cctvName = "",
                    deactive = deactive,
                    ipAddress = ""
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
                        ipAddress = search
                    )
                )
            } else {
                //Not valid IP Address means search for name
                viewModel.findCctvFromServer(
                    FindCctvDto(
                        branch = branch,
                        cctvName = search,
                        deactive = deactive,
                        ipAddress = ""
                    )
                )
            }
        }
    }

    private fun intentToStockAppendActivity() {
        val intent = Intent(this, AppendStockActivity::class.java)
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

//    private fun validateJsonStringInSharedPrefsForDropdown() {
//        if (JsonMarshaller().getOption() != null) {
//            optionJsonObject = JsonMarshaller().getOption()!!
//        } else {
//            Toasty.error(this, ERR_DROPDOWN_NOT_LOAD, Toasty.LENGTH_LONG).show()
//        }
//    }


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
            findCctvs()
            App.activityStockListMustBeRefresh = false
        }
    }
}

