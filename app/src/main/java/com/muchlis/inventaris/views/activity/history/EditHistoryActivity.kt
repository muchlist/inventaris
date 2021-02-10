package com.muchlis.inventaris.views.activity.history

import android.annotation.SuppressLint
import android.os.Bundle
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
        bd.tvHistoryName.text = data.category + " " + data.parentName
        bd.tvHistoryStatus.text = data.status
        bd.etfDesc.editText?.setText(data.note)
    }

    private fun sendDataToServer(data: HistoryResponse) {

        //TODO
        /*
        * samakan dengan halaman menambah history agar tidak repot
        * ada pilihan pending dan complete pada complete status*/

        val timeNow = Calendar.getInstance().time
        val dateText = timeNow.toStringInputDate()

        val args = HistoryEditRequest(
            resolveNote = bd.etfResolveNote.editText?.text.toString(),
            note = bd.etfDesc.editText?.text.toString(),
            status = data.status,
            date = data.date,
            endDate = dateText,
            location = data.location,
            completeStatus = 2, //TODO
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