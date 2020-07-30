package com.muchlis.inventaris.views.activity.history

import android.R.layout.simple_spinner_dropdown_item
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityAppendHistoryBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.AppendHistoryViewModel
import es.dmoral.toasty.Toasty
import java.util.*


class AppendHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: AppendHistoryViewModel
    private lateinit var bd: ActivityAppendHistoryBinding

    private lateinit var optionJsonObject: SelectOptionResponse


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityAppendHistoryBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(AppendHistoryViewModel::class.java)
        observeViewModel()

        val parentID = intent.getStringExtra(INTENT_TO_HISTORY_CREATE_ID)
        val parentCategory = intent.getStringExtra(INTENT_TO_HISTORY_CREATE_CATEGORY)
        val parentName = intent.getStringExtra(INTENT_TO_HISTORY_CREATE_NAME)

        bd.etfHistoryName.editText?.setText(parentName)

        if (JsonMarshaller().getOption() != null) {
            optionJsonObject = JsonMarshaller().getOption()!!
        } else {
            Toasty.error(this, ERR_DROPDOWN_NOT_LOAD, Toasty.LENGTH_LONG).show()
        }

        setAutoTextStatus(optionJsonObject.history)

        bd.btSave.setOnClickListener {
            sendDataToServer(
                parentID = parentID,
                parentCategory = parentCategory
            )
        }
        bd.ivApplyButton.setOnClickListener {
            sendDataToServer(
                parentID = parentID,
                parentCategory = parentCategory
            )
        }
        bd.ivBackButton.setOnClickListener { onBackPressed() }
    }

    private fun setAutoTextStatus(status: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            simple_spinner_dropdown_item,
            status
        )

        bd.atHistoryStatus.setAdapter(adapter)
    }

    private fun sendDataToServer(parentID: String?, parentCategory: String?) {
        val timeNow = Calendar.getInstance().time
        val dateText = timeNow.toStringInputDate()

        val args = HistoryRequest(
            category = parentCategory ?: "",
            date = dateText,
            note = bd.etfNote.editText?.text.toString(),
            status = bd.atHistoryStatus.text.toString()
        )

        if (args.isValid()) {
            viewModel.appendHistory(parentID ?: "", args)
        } else {
            Toasty.error(this, ERR_FORM_NOT_VALID).show()
        }
    }

    private fun observeViewModel() {

        viewModel.run {
            isHistoryCreated.observe(this@AppendHistoryActivity, Observer {
                killActivityIfHistoryCreated(it)
            })
            isLoading.observe(this@AppendHistoryActivity, Observer { showLoading(it) })
            messageError.observe(this@AppendHistoryActivity, Observer { showToast(it, true) })
            messageSuccess.observe(this@AppendHistoryActivity, Observer { showToast(it, false) })
        }
    }

    private fun killActivityIfHistoryCreated(isCreated: Boolean) {
        if (isCreated) {
            App.activityDashboardMustBeRefresh = true
            App.fragmentDetailComputerMustBeRefresh = true
            App.fragmentHistoryComputerMustBeRefresh = true
            finish()
        }
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