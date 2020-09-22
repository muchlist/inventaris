package com.muchlis.inventaris.views.activity.pelindo_apps_history

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.dto.FindAppHistoryDto
import com.muchlis.inventaris.data.response.HistoryAppsListResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityPelindoAppsHistoryListBinding
import com.muchlis.inventaris.recycler_adapter.AppHistoryAdapter
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.invisible
import com.muchlis.inventaris.utils.visible
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

        bd.fabPelindoAppHistory.setOnClickListener {
            startActivity(Intent(this, AppendPelindoAppsHistoryActivity::class.java))
        }

        bd.chipFilter.setOnClickListener {
            //TODO showFilterDialog()
        }
    }

    private fun findHistory(
        appName: String = "",
        branch: String = "",
    ) {
        viewModel.findHistories(
            FindAppHistoryDto(
                appName = appName,
                branch = branch,
                limit = 100
            )
        )
    }

    private fun observeViewModel() {

        viewModel.run {
            getHistoryData().observe(this@PelindoAppsHistoryListActivity, Observer {
                loadRecyclerView(it)
            })
            isLoading.observe(this@PelindoAppsHistoryListActivity, Observer { showLoading(it) })
            messageError.observe(
                this@PelindoAppsHistoryListActivity,
                Observer { showErrorToast(it) })
        }
    }

    private fun setRecyclerView() {
        bd.rvDetailAppHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        appHistoryAdapter = AppHistoryAdapter(this, historyData) {
            //TODO intentToComputerDetailActivity(it.id)
        }
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

    private fun showLoading(isLoading: Boolean) {
        bd.refreshDetailAppHistory.isRefreshing = isLoading
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(this, text, Toasty.LENGTH_LONG).show()
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