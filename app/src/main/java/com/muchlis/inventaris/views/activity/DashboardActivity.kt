package com.muchlis.inventaris.views.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.GridLayoutAnimationController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.dto.MenuData
import com.muchlis.inventaris.data.dto.FindHistoryDto
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.databinding.ActivityDashboardBinding
import com.muchlis.inventaris.recycler_adapter.DashboardMenuAdapter
import com.muchlis.inventaris.recycler_adapter.HistoryAdapter
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.INTENT_PC_TO_DETAIL
import com.muchlis.inventaris.utils.invisible
import com.muchlis.inventaris.utils.visible
import com.muchlis.inventaris.view_model.DashboardViewModel
import es.dmoral.toasty.Toasty

class DashboardActivity : AppCompatActivity() {

    private lateinit var bd: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel

    //gridview
    private lateinit var menuAdapter: DashboardMenuAdapter
    private var menuDataData: MutableList<MenuData> = mutableListOf()

    //recyclerview
    private lateinit var historyAdapter: HistoryAdapter
    private var historyData: MutableList<HistoryResponse> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityDashboardBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)


        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        observeViewModel()

        setToolbarTitle()

        setMenuDataForListMenu()

        setListMenu()

        setRecyclerView()

        findHistories()
        viewModel.getOption()
    }

    private fun observeViewModel() {

        viewModel.run {
            getHistoryData().observe(this@DashboardActivity, Observer {
                loadRecyclerView(it)
                showLoadMoreButton(it.histories.count())
            })
            isLoading.observe(this@DashboardActivity, Observer { showLoading(it) })
            messageError.observe(this@DashboardActivity, Observer { showErrorToast(it) })
        }
    }

    private fun setMenuDataForListMenu() {
        menuDataData.apply {
            clear()
            add(
                MenuData(
                    0,
                    "Komputer",
                    R.drawable.ic_029_computer
                )
            )
            add(
                MenuData(
                    1,
                    "Stok",
                    R.drawable.ic_049_stock
                )
            )
            add(
                MenuData(
                    2,
                    "Printer",
                    R.drawable.ic_041_printer
                )
            )
            add(
                MenuData(
                    3,
                    "Server",
                    R.drawable.ic_047_server
                )
            )
            add(
                MenuData(
                    4,
                    "CCTV",
                    R.drawable.ic_018_cctv
                )
            )
        }
    }

    private fun setListMenu() {

        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.grid_item_anim)
        val controller = GridLayoutAnimationController(animation, .1f, .3f)

        menuAdapter = DashboardMenuAdapter(this, menuDataData) {

            when (it.id) {
                0 -> intentToComputerActivity()
//                1 -> startActivity<StockListActivity>()
//                2 -> startActivity<PrinterListActivity>()
//                3 -> startActivity<ServerListActivity>()
//                4 -> startActivity<CctvListActivity>()
                else -> {
                }
            }
        }
        bd.gvDashboardMenu.layoutAnimation = controller
        bd.gvDashboardMenu.adapter = menuAdapter
    }

    private fun intentToComputerActivity() {
        val intent = Intent(this, ComputersActivity::class.java)
        startActivity(intent)
    }

    private fun setRecyclerView() {
        bd.rvHistoryDashboard.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter(this, historyData) {
            intentToComputerDetailActivity(parentID = it.parentId, category = it.category)
        }
        bd.rvHistoryDashboard.adapter = historyAdapter
        bd.rvHistoryDashboard.setHasFixedSize(true)
    }

    private fun loadRecyclerView(data: HistoryListResponse) {
        historyData.clear()
        historyData.addAll(data.histories)
        runLayoutAnimation()
        historyAdapter.notifyDataSetChanged()
    }

    private fun intentToComputerDetailActivity(parentID: String, category: String) {
        when (category) {
            "PC" -> {
                val intent = Intent(this, ComputerDetailActivity::class.java)
                intent.putExtra(INTENT_PC_TO_DETAIL, parentID)
                startActivity(intent)
            }
            else -> {
                Toasty.error(this, "Category tidak valid", Toasty.LENGTH_LONG).show()
            }
        }
    }

    private fun runLayoutAnimation() {
        bd.rvHistoryDashboard.scheduleLayoutAnimation()
        bd.rvHistoryDashboard.invalidate()
    }

    private fun findHistories() {
        viewModel.findHistories(
            FindHistoryDto(
                branch = App.prefs.userBranchSave,
                category = "",
                limit = 5
            )
        )
    }

    private fun showLoadMoreButton(dataCount: Int) {
        if (dataCount != 0) {
            bd.btHistoryDashboard.visible()
        }
    }

    private fun setToolbarTitle() {
        bd.collapsingToolbar.title = App.prefs.nameSave
        bd.collapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsedAppBar)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bd.pbHistoryDashboard.visible()
        } else {
            bd.pbHistoryDashboard.invisible()
        }
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(this, text, Toasty.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.activityDashboardMustBeRefresh) {
            findHistories()
            App.fragmentHistoryComputerMustBeRefresh = false
        }
    }
}