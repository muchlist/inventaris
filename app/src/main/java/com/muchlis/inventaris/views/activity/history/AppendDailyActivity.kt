package com.muchlis.inventaris.views.activity.history

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.databinding.ActivityAppendDailyBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.history.AppendHistoryViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class AppendDailyActivity : AppCompatActivity() {

    private lateinit var viewModel: AppendHistoryViewModel
    private lateinit var bd: ActivityAppendDailyBinding

    private lateinit var parentCategory: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityAppendDailyBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(AppendHistoryViewModel::class.java)
        observeViewModel()

        val parentID = App.prefs.usernameSave
        parentCategory = CATEGORY_DAILY
        val parentName = App.prefs.nameSave

        bd.etfHistoryName.editText?.setText(parentName)

        setAutoTextStatus(listOf(App.prefs.lastTitleDaily, "Daily"))

        bd.btSave.setOnClickListener {
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
            android.R.layout.simple_spinner_dropdown_item,
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
            isHistoryCreated.observe(this@AppendDailyActivity, {
                if (it) {
                    App.prefs.lastTitleDaily = bd.atHistoryStatus.text.toString()
                }
                killActivityIfHistoryCreated(it)
            })
            isLoading.observe(
                this@AppendDailyActivity, { showLoading(it) })
            messageError.observe(
                this@AppendDailyActivity, { showToast(it, true) })
            messageSuccess.observe(
                this@AppendDailyActivity, { showToast(it, false) })
        }
    }

    private fun killActivityIfHistoryCreated(isCreated: Boolean) {
        if (isCreated) {
            App.activityDashboardMustBeRefresh = true
            App.fragmentHistoryComputerMustBeRefresh = true
            when (parentCategory) {
                CATEGORY_CCTV -> {
                    App.fragmentDetailCctvMustBeRefresh = true
                    App.activityCctvListMustBeRefresh = true
                }
                CATEGORY_PC -> App.fragmentDetailComputerMustBeRefresh = true
            }
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bd.btSave.invisible()
        } else {
            bd.btSave.visible()
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