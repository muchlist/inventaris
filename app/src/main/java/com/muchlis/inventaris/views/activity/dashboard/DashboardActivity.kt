package com.muchlis.inventaris.views.activity.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.zxing.integration.android.IntentIntegrator
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.DashboardResponse
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.databinding.ActivityDashboardBinding
import com.muchlis.inventaris.recycler_adapter.HistoryAdapter
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.DashboardViewModel
import com.muchlis.inventaris.views.activity.cctv.CctvDetailActivity
import com.muchlis.inventaris.views.activity.cctv.CctvsActivity
import com.muchlis.inventaris.views.activity.computer.ComputerDetailActivity
import com.muchlis.inventaris.views.activity.computer.ComputersActivity
import com.muchlis.inventaris.views.activity.handheld.HandheldDetailActivity
import com.muchlis.inventaris.views.activity.handheld.HandheldsActivity
import com.muchlis.inventaris.views.activity.history.AppendDailyActivity
import com.muchlis.inventaris.views.activity.history.EditHistoryActivity
import com.muchlis.inventaris.views.activity.history.HistoryListActivity
import com.muchlis.inventaris.views.activity.pelindo_apps_history.PelindoAppsHistoryListActivity
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

        viewModel.getDashboardHistoriesFromServer()

        bd.menuPc.setOnClickListener { intentToComputerActivity() }
        bd.menuStock.setOnClickListener { intentToStockActivity() }
        bd.menuCctv.setOnClickListener { intentToCctvActivity() }
        bd.menuQr.setOnClickListener { intentToQrCode() }
        bd.menuDaily.setOnClickListener { intentToAppendDailyActivity() }
        bd.menuApplication.setOnClickListener { intentToAppsHistoryActivity() }
        bd.menuHandheld.setOnClickListener { intentToHandheldActivity() }

        bd.ivReload.setOnClickListener {
            viewModel.getDashboardHistoriesFromServer()
        }

        bd.btHistoryDashboard.setOnClickListener {
            intentToHistoryListActivity()
        }

        bd.infoBar.setOnClickListener {
            intentToHistoryListActivity(isComplete = 0)
        }
    }

    private fun observeViewModel() {

        viewModel.run {
            getDashboardData().observe(this@DashboardActivity, {
                loadRecyclerView(it.histories)
                showLoadMoreButton(it.histories.count())
                loadInfoProblem(it)
            })
            isLoading.observe(this@DashboardActivity, { showLoading(it) })
            messageError.observe(this@DashboardActivity, { showErrorToast(it) })
            isHistoryDeleted.observe(this@DashboardActivity, {
                if (it) {
                    viewModel.getDashboardHistoriesFromServer()
                }
            })
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
                super.onActivityResult(requestCode, resultCode, data)
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
                CATEGORY_APPLICATION -> {
                }
                CATEGORY_TABLET -> intentToHandheldDetailActivity(textList[1])
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

    private fun intentToHandheldActivity() {
        val intent = Intent(this, HandheldsActivity::class.java)
        startActivity(intent)
    }


    private fun intentToAppsHistoryActivity() {
        val intent = Intent(this, PelindoAppsHistoryListActivity::class.java)
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

    private fun intentToHandheldDetailActivity(unitID: String) {
        val intent = Intent(this, HandheldDetailActivity::class.java)
        intent.putExtra(INTENT_HH_TO_DETAIL, unitID)
        startActivity(intent)
    }

    private fun intentToAppendDailyActivity() {
        val intent = Intent(this, AppendDailyActivity::class.java)
        startActivity(intent)
    }

    private fun intentToHistoryListActivity(isComplete: Int = 100) {
        val intent = Intent(this, HistoryListActivity::class.java)
        intent.putExtra(INTENT_TO_HISTORY_LIST, isComplete)
        startActivity(intent)
    }

    private fun setRecyclerView() {
        bd.rvHistoryDashboard.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val onClickItem: (HistoryResponse) -> Unit = {
            if (!it.isComplete && it.branch == App.prefs.userBranchSave) {
                val intent = Intent(this, EditHistoryActivity::class.java)
                intent.putExtra(INTENT_TO_HISTORY_EDIT, it)
                startActivity(intent)
            }
        }

        val onLongClickItem: (HistoryResponse) -> Unit = {
            if (it.category == CATEGORY_DAILY) {
                //Khusus Daily perilakunya delete daily
                if (it.author == App.prefs.nameSave) {
                    deleteDailyHistory(it.id)
                }
            } else {
                intentToDetailActivity(parentID = it.parentId, category = it.category)
            }
        }

        historyAdapter = HistoryAdapter(this, historyData, onClickItem, onLongClickItem)
        bd.rvHistoryDashboard.adapter = historyAdapter
        bd.rvHistoryDashboard.setHasFixedSize(true)
    }

    private fun loadRecyclerView(data: List<HistoryResponse>) {
        historyData.clear()
        historyData.addAll(data)
        runLayoutAnimation()
        historyAdapter.notifyDataSetChanged()
    }

    private fun runLayoutAnimation() {
        bd.rvHistoryDashboard.scheduleLayoutAnimation()
        bd.rvHistoryDashboard.invalidate()
    }

    @SuppressLint("SetTextI18n")
    private fun loadInfoProblem(data: DashboardResponse) {
        bd.chipDashboard.removeAllViews()
        //DETAIL
        for (info in data.issues) {
            val chip = Chip(bd.chipDashboard.context)
            chip.text = "${info.id} : ${info.count} issue"
            chip.isClickable = false
            chip.isCheckable = false
            bd.chipDashboard.addView(chip)
        }
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
            CATEGORY_APPLICATION -> {
            }
            CATEGORY_TABLET -> {
                intentToHandheldDetailActivity(parentID)
            }
            else -> {
                Toasty.error(this, "Category tidak valid", Toasty.LENGTH_LONG).show()
            }
        }
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

    private fun deleteDailyHistory(historyID: String) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Konfirmasi")
        builder.setMessage("Yakin ingin menghapus daily?")

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
            viewModel.getDashboardHistoriesFromServer()
            App.activityDashboardMustBeRefresh = false
        }
    }
}