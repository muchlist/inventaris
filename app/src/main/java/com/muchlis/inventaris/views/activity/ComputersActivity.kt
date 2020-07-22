package com.muchlis.inventaris.views.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.dto.FindComputersDto
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.databinding.ActivityComputerBinding
import com.muchlis.inventaris.recycler_adapter.ComputerAdapter
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.INTENT_PC_TO_DETAIL
import com.muchlis.inventaris.view_model.ComputersViewModel
import es.dmoral.toasty.Toasty

class ComputersActivity : AppCompatActivity() {

    private lateinit var bd: ActivityComputerBinding
    private lateinit var viewModel: ComputersViewModel

    //recyclerview
    private lateinit var computerAdapter: ComputerAdapter
    private var computerData: MutableList<ComputerListResponse.Computer> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityComputerBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(ComputersViewModel::class.java)

        observeViewModel()

        setRecyclerView()
        bd.refreshComputerList.setOnRefreshListener {
            findComputers()
        }

        findComputers()

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

    private fun findComputers() {
        viewModel.findComputersFromServer(
            FindComputersDto(
                branch = App.prefs.userBranchSave,
                ipAddress = "",
                clientName = ""
            )
        )
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
        runLayoutAnimation()
        computerAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
//        if (it.containers.count() == 0){
//            view?.iv_level_one_empty?.visible()
//        } else {
//            view?.iv_level_one_empty?.invisible()
//        }
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
        if (App.activityComputerListMustBeRefresh){
            findComputers()
            App.activityComputerListMustBeRefresh = false
        }
    }
}