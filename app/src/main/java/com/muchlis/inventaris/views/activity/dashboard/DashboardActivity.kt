package com.muchlis.inventaris.views.activity.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.dto.FindHistoryDto
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.databinding.ActivityDashboardBinding
import com.muchlis.inventaris.recycler_adapter.HistoryAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.DashboardViewModel
import com.muchlis.inventaris.views.activity.cctv.CctvDetailActivity
import com.muchlis.inventaris.views.activity.cctv.CctvsActivity
import com.muchlis.inventaris.views.activity.computer.ComputerDetailActivity
import com.muchlis.inventaris.views.activity.computer.ComputersActivity
import com.muchlis.inventaris.views.activity.stock.StockDetailActivity
import com.muchlis.inventaris.views.activity.stock.StocksActivity
import es.dmoral.toasty.Toasty
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var bd: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel

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

        setRecyclerView()

        findHistories()
        viewModel.getOption()

        bd.menuPc.setOnClickListener { intentToComputerActivity() }
        bd.menuStock.setOnClickListener { intentToStockActivity() }
        bd.menuCctv.setOnClickListener { intentToCctvActivity() }
        bd.menuQr.setOnClickListener { intentToQrCode() }

        bd.ivReload.setOnClickListener {
            findHistories()
        }
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


    //ON RESULT SCAN QR CODE
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {

            //CODING FROM LIBRARY XZINK START
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    //CANCEL QR SCAN
                    showErrorToast("Scan QR dibatalkan")
                } else {
                    //SUCCESS QR SCAN = result.contents
                    detectCategoryScannedByQRCode(result.contents)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            //CODING FROM LIBRARY XZINK END
        }
    }

    private fun detectCategoryScannedByQRCode(scannedText: String) {
        val textList = scannedText.split(" ")
        if (textList.count() == 2) {
            when (textList[0].toUpperCase(Locale.ROOT)) {
                CATEGORY_PC -> intentToComputerDetailActivity(textList[1])
                CATEGORY_CCTV -> intentToCctvDetailActivity(textList[1])
                CATEGORY_STOCK -> intentToStockDetailActivity(textList[1])
                else -> showErrorToast("QR Code tidak dikenali")
            }
        } else {
            showErrorToast("QR Code tidak dikenali")
        }
    }

    private fun intentToComputerActivity() {
        val intent = Intent(this, ComputersActivity::class.java)
        startActivity(intent)
    }

    private fun intentToStockActivity() {
        val intent = Intent(this, StocksActivity::class.java)
        startActivity(intent)
    }

    private fun intentToCctvActivity() {
        val intent = Intent(this, CctvsActivity::class.java)
        startActivity(intent)
    }

    private fun intentToQrCode() {
        val scanner = IntentIntegrator(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(false)
        scanner.initiateScan()
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

    private fun setRecyclerView() {
        bd.rvHistoryDashboard.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter(this, historyData) {
            intentToDetailActivity(parentID = it.parentId, category = it.category)
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
            App.activityDashboardMustBeRefresh = false
        }
    }
}