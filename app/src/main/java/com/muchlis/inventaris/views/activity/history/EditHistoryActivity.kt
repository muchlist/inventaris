package com.muchlis.inventaris.views.activity.history

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.HistoryEditRequest
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.databinding.ActivityEditHistoryBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.history.EditHistoryViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class EditHistoryActivity : AppCompatActivity() {

    private var data: HistoryResponse? = null

    private lateinit var viewModel: EditHistoryViewModel
    private lateinit var bd: ActivityEditHistoryBinding

    private val completeStatusOption = listOf<String>("Progress", "Pending", "Complete")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityEditHistoryBinding.inflate(layoutInflater)
        setContentView(bd.root)

        data = intent.getParcelableExtra<HistoryResponse>(
            INTENT_TO_HISTORY_EDIT
        )

        viewModel = ViewModelProvider(this).get(EditHistoryViewModel::class.java)
        observeViewModel()

        data?.let {
            setDataToLayout(it)
        }

        //CompleteStatus
        setAutoTextCompleteStatus()

        bd.btSave.setOnClickListener {
            data?.let { sendDataToServer(it) }
        }
        bd.ivApplyButton.setOnClickListener {
            data?.let { sendDataToServer(it) }
        }
        bd.ivBackButton.setOnClickListener { onBackPressed() }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToLayout(data: HistoryResponse) {
        bd.etfHistoryName.editText?.setText(data.category + " " + data.parentName)
        bd.etfHistoryStatus.editText?.setText(data.status)
        bd.etfNote.editText?.setText(data.note)
    }

    private fun setAutoTextCompleteStatus() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.simple_spinner_dropdown_item,
            completeStatusOption
        )

        bd.atHistoryCompleteStatus.setText(completeStatusOption[data?.completeStatus?:0])
        bd.atHistoryCompleteStatus.setAdapter(adapter)

    }

    private fun sendDataToServer(data: HistoryResponse) {

        val timeNow = Calendar.getInstance().time
        val dateText = timeNow.toStringInputDate()

        var completeStatus = 0
        if (bd.atHistoryCompleteStatus.text.toString().isNotEmpty()){
            completeStatus = completeStatusOption.indexOf(bd.atHistoryCompleteStatus.text.toString())
        }

        val args = HistoryEditRequest(
            resolveNote = bd.etfResolveNote.editText?.text.toString(),
            note = bd.etfNote.editText?.text.toString(),
            status = data.status,
            date = data.date,
            endDate = dateText,
            location = data.location,
            completeStatus = completeStatus,
            timestamp = data.timestamp,
            category = data.category
        )

        viewModel.editHistoryR(data.id, args)
    }

    private fun observeViewModel() {

        viewModel.run {
            isHistoryEdited.observe(this@EditHistoryActivity, {
                killActivityIfHistoryEditedSuccessfully(it)
            })
            isLoading.observe(this@EditHistoryActivity, { showLoading(it) })
            messageError.observe(this@EditHistoryActivity, { showToast(it, true) })
            messageSuccess.observe(this@EditHistoryActivity, { showToast(it, false) })
        }
    }

    private fun killActivityIfHistoryEditedSuccessfully(isSuccess: Boolean) {
        if (isSuccess) {
            App.activityHistoryListMustBeRefresh = true
            App.activityDashboardMustBeRefresh = true
            App.fragmentHistoryAllMustBeRefresh = true

            when (data?.category) {
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