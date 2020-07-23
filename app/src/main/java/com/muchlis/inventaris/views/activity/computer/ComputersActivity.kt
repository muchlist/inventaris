package com.muchlis.inventaris.views.activity.computer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.dto.FindComputersDto
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.databinding.ActivityComputerBinding
import com.muchlis.inventaris.recycler_adapter.ComputerAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.ComputersViewModel
import es.dmoral.toasty.Toasty

class ComputersActivity : AppCompatActivity() {

    private lateinit var bd: ActivityComputerBinding
    private lateinit var viewModel: ComputersViewModel

    //recyclerview
    private lateinit var computerAdapter: ComputerAdapter
    private var computerData: MutableList<ComputerListResponse.Computer> = mutableListOf()

    //first time animation
    private var isFirstTimeLoad = true

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

    private fun setSearchBarListener(){
        bd.etComputerlistSearchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()){
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
        if (data.computers.count() == 0){
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