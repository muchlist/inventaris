package com.muchlis.inventaris.views.activity.stock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.StockUseRequest
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.databinding.ActivityStockUseBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.stock.StockUseViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class StockUseActivity : AppCompatActivity() {

    private lateinit var viewModel: StockUseViewModel
    private lateinit var bd: ActivityStockUseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityStockUseBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(StockUseViewModel::class.java)
        observeViewModel()

        val parentID = intent.getStringExtra(INTENT_TO_STOCK_USE_CREATE_ID) ?: ""
        val parentName = intent.getStringExtra(INTENT_TO_STOCK_USE_NAME) ?: ""
        val mode = intent.getStringExtra(INTENT_TO_STOCK_USE_MODE) ?: ""


        if (mode == INCREMENT_MODE) {
            bd.tvHeader.text = "Penambahan stok"
        } else {
            bd.tvHeader.text = "Pengurangan stok"
        }
        bd.etfStockName.editText?.setText(parentName)

        bd.btSave.setOnLongClickListener {
            sendDataToServer(
                parentID = parentID,
                mode = mode
            )
            return@setOnLongClickListener false
        }
        bd.ivApplyButton.setOnLongClickListener {
            sendDataToServer(
                parentID = parentID,
                mode = mode
            )
            return@setOnLongClickListener false
        }
        bd.ivBackButton.setOnClickListener { onBackPressed() }
    }

    private fun sendDataToServer(parentID: String, mode: String) {
        val timeNow = Calendar.getInstance().time
        val dateText = timeNow.toStringInputDate()

        val args = StockUseRequest(
            mode = mode,
            baNumber = bd.etfStockBaNumber.editText?.text.toString(),
            time = dateText,
            note = bd.etfStockNote.editText?.text.toString(),
            qty = bd.etfStockQty.editText?.text.toString().toDoubleOrNull() ?: 0.0
        )

        if (args.isValid()) {
            viewModel.useStockFromServer(parentID, args)
        } else {
            Toasty.error(this, ERR_FORM_NOT_VALID).show()
        }
    }

    private fun observeViewModel() {

        viewModel.run {
            isStockUseCreated.observe(this@StockUseActivity, androidx.lifecycle.Observer {
                if (it) {
                    intentSetResult(viewModel.retrieveStockDetail())
                    killActivityIfHistoryCreated()
                }

            })
            isLoading.observe(
                this@StockUseActivity,
                androidx.lifecycle.Observer { showLoading(it) })
            messageError.observe(
                this@StockUseActivity,
                androidx.lifecycle.Observer { showToast(it, true) })
            messageSuccess.observe(
                this@StockUseActivity,
                androidx.lifecycle.Observer { showToast(it, false) })
        }
    }

    private fun killActivityIfHistoryCreated() {
        App.activityStockListMustBeRefresh = true
        App.activityDashboardMustBeRefresh = true
        finish()
    }

    private fun intentSetResult(data: StockDetailResponse) {
        val intent = Intent()
        intent.putExtra(INTENT_RESULT_STOCK, data)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bd.btSave.invisible()
            bd.ivApplyButton.invisible()
        } else {
            bd.btSave.visible()
            bd.ivApplyButton.visible()
        }
    }

    private fun showToast(text: String, isError: Boolean = false) {
        if (text.isNotEmpty()) {
            if (isError) {
                Toasty.error(this, text, Toasty.LENGTH_LONG).show()
            } else {
                Toasty.success(this, text, Toasty.LENGTH_LONG).show()
            }
        }
    }
}