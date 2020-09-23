package com.muchlis.inventaris.views.activity.pelindo_apps_history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.HistoryAppsEditRequest
import com.muchlis.inventaris.data.response.HistoryAppsListResponse
import com.muchlis.inventaris.databinding.ActivityEditPelindoAppsHistoryBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.pelindo_app_history.EditPelindoAppsHistoryViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class EditPelindoAppsHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: EditPelindoAppsHistoryViewModel
    private lateinit var bd: ActivityEditPelindoAppsHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityEditPelindoAppsHistoryBinding.inflate(layoutInflater)
        setContentView(bd.root)

        val data = intent.getParcelableExtra<HistoryAppsListResponse.History>(
            INTENT_PELINDO_HISTORY_TO_EDIT
        )
        viewModel = ViewModelProvider(this).get(EditPelindoAppsHistoryViewModel::class.java)
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

    private fun setDataToLayout(data: HistoryAppsListResponse.History) {
        bd.tvAppHistoryName.text = data.parentName
        bd.tvAppHistoryStatus.text = data.status
        bd.etfDesc.editText?.setText(data.desc)
    }

    private fun sendDataToServer(data: HistoryAppsListResponse.History) {
        val timeNow = Calendar.getInstance().time
        val dateText = timeNow.toStringInputDate()

        val args = HistoryAppsEditRequest(
            title = data.title,
            resolveNote = bd.etfResolveNote.editText?.text.toString(),
            desc = bd.etfDesc.editText?.text.toString(),
            status = data.status,
            startDate = data.startDate,
            endDate = dateText,
            location = "",
            isComplete = true,
            pic = "",
            timestamp = data.updatedAt
        )

        viewModel.editHistory(data.id, args)
    }

    private fun observeViewModel() {

        viewModel.run {
            isHistoryEdited.observe(this@EditPelindoAppsHistoryActivity, {
                killActivityIfHistoryEditedSuccessfully(it)
            })
            isLoading.observe(this@EditPelindoAppsHistoryActivity, { showLoading(it) })
            messageError.observe(this@EditPelindoAppsHistoryActivity, { showToast(it, true) })
            messageSuccess.observe(this@EditPelindoAppsHistoryActivity, { showToast(it, false) })
        }
    }

    private fun killActivityIfHistoryEditedSuccessfully(isSuccess: Boolean) {
        if (isSuccess) {
            App.activityAppsHistoryListMustBeRefresh = true
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