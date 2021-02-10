package com.muchlis.inventaris.views.activity.history

import android.R.layout.simple_spinner_dropdown_item
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityAppendHistoryBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.history.AppendHistoryViewModel
import es.dmoral.toasty.Toasty
import java.util.*


class AppendHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: AppendHistoryViewModel
    private lateinit var bd: ActivityAppendHistoryBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    private lateinit var parentCategory: String

    private val completeStatusOption = listOf<String>("Progress", "Pending", "Complete")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityAppendHistoryBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(AppendHistoryViewModel::class.java)
        observeViewModel()

        val parentID = intent.getStringExtra(INTENT_TO_HISTORY_CREATE_ID)
        parentCategory = intent.getStringExtra(INTENT_TO_HISTORY_CREATE_CATEGORY) ?: ""
        val parentName = intent.getStringExtra(INTENT_TO_HISTORY_CREATE_NAME)

        bd.etfHistoryName.editText?.setText(parentName)

        if (JsonMarshaller().getOption() != null) {
            optionJsonObject = JsonMarshaller().getOption()!!
        } else {
            Toasty.error(this, ERR_DROPDOWN_NOT_LOAD, Toasty.LENGTH_LONG).show()
        }

        //Status Selector
        if (parentCategory == CATEGORY_CCTV){
            setAutoTextStatus(optionJsonObject.cctvHistory)
        }else{
            setAutoTextStatus(optionJsonObject.history)
        }

        //CompleteStatus
        setAutoTextCompleteStatus()

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

//        bd.swHistoryComplete.setOnClickListener {
//            if (bd.swHistoryComplete.isChecked) {
//                bd.etfResolveNote.visible()
//            } else {
//                bd.etfResolveNote.invisible()
//            }
//        }
    }

    private fun setAutoTextStatus(status: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            simple_spinner_dropdown_item,
            status
        )

        bd.atHistoryStatus.setAdapter(adapter)
    }

    private fun setAutoTextCompleteStatus() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            simple_spinner_dropdown_item,
            completeStatusOption
        )

        bd.atHistoryCompleteStatus.setText(completeStatusOption[0])
        bd.atHistoryCompleteStatus.setAdapter(adapter)

    }

    private fun sendDataToServer(parentID: String?, parentCategory: String?) {
        val timeNow = Calendar.getInstance().time
        val dateText = timeNow.toStringInputDate()

        val endDate: String?
        val resolveNote: String

        var completeStatus = 0
        if (bd.atHistoryCompleteStatus.text.toString().isNotEmpty()){
            completeStatus = completeStatusOption.indexOf(bd.atHistoryCompleteStatus.text.toString())
        }
        // if not complete endDate must be null
        if (completeStatus == 2) {
            endDate = dateText
            resolveNote = bd.etfResolveNote.editText?.text.toString()
        } else {
            endDate = null
            resolveNote = ""
        }


        val args = HistoryRequest(
            category = parentCategory ?: "",
            date = dateText,
            note = bd.etfNote.editText?.text.toString(),
            status = bd.atHistoryStatus.text.toString(),

            resolveNote = resolveNote,
            endDate = endDate,
            location = App.prefs.userBranchSave,
            completeStatus = completeStatus,
        )

        if (args.isValid()) {
            viewModel.appendHistory(parentID ?: "", args)
        } else {
            Toasty.error(this, ERR_FORM_NOT_VALID).show()
        }
    }

    private fun observeViewModel() {

        viewModel.run {
            isHistoryCreated.observe(this@AppendHistoryActivity, {
                killActivityIfHistoryCreated(it)
            })
            isLoading.observe(this@AppendHistoryActivity, { showLoading(it) })
            messageError.observe(this@AppendHistoryActivity, { showToast(it, true) })
            messageSuccess.observe(this@AppendHistoryActivity, { showToast(it, false) })
        }
    }

    private fun killActivityIfHistoryCreated(isCreated: Boolean) {
        if (isCreated) {
            App.activityDashboardMustBeRefresh = true
            App.fragmentHistoryAllMustBeRefresh = true
            when (parentCategory) {
                CATEGORY_CCTV -> {
                    App.fragmentDetailCctvMustBeRefresh = true
                    App.activityCctvListMustBeRefresh = true
                }
                CATEGORY_PC -> App.fragmentDetailComputerMustBeRefresh = true
                CATEGORY_TABLET -> App.fragmentDetailHHMustBeRefresh = true
            }
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