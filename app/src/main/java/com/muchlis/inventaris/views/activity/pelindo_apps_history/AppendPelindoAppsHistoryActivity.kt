package com.muchlis.inventaris.views.activity.pelindo_apps_history

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.HistoryAppsRequest
import com.muchlis.inventaris.data.response.PelindoAppsListResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityAppendPelindoAppsHistoryBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.pelindo_app_history.AppendPelindoAppsHistoryViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class AppendPelindoAppsHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: AppendPelindoAppsHistoryViewModel
    private lateinit var bd: ActivityAppendPelindoAppsHistoryBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    private lateinit var pelindoAppsListObject: PelindoAppsListResponse
    private var pelindoAppListString = mutableListOf<String>()
    private var pelindoAppListIDString = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityAppendPelindoAppsHistoryBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(AppendPelindoAppsHistoryViewModel::class.java)
        observeViewModel()

        if (JsonMarshaller().getOption() != null) {
            optionJsonObject = JsonMarshaller().getOption()!!
        } else {
            Toasty.error(this, ERR_DROPDOWN_NOT_LOAD, Toasty.LENGTH_LONG).show()
        }

        viewModel.findApps()

        setAutoTextStatus(optionJsonObject.appHistory)

        bd.btSave.setOnClickListener {
            sendDataToServer()
        }
        bd.ivApplyButton.setOnClickListener {
            sendDataToServer()
        }

        bd.swAppHistoryComplete.setOnClickListener {
            if (bd.swAppHistoryComplete.isChecked) {
                bd.etfResolveNote.visible()
            } else {
                bd.etfResolveNote.invisible()
            }
        }

        bd.ivBackButton.setOnClickListener { onBackPressed() }
    }

    private fun setAutoTextApps(status: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            status
        )
        bd.atAppHistoryAppsName.setAdapter(adapter)
    }

    private fun setAutoTextStatus(status: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            status
        )

        bd.atAppHistoryStatus.setAdapter(adapter)
    }

    private fun sendDataToServer() {
        val timeNow = Calendar.getInstance().time
        val dateText = timeNow.toStringInputDate()

        val endDate: String?
        val resolveNote: String
        if (bd.swAppHistoryComplete.isChecked) {
            endDate = dateText
            resolveNote = bd.etfResolveNote.editText?.text.toString()
        } else {
            endDate = null
            resolveNote = ""
        }

        val args = HistoryAppsRequest(
            title = bd.atAppHistoryStatus.text.toString(),
            desc = bd.etfDesc.editText?.text.toString(),
            resolveNote = resolveNote,
            status = bd.atAppHistoryStatus.text.toString(),
            startDate = dateText,
            endDate = endDate,
            location = App.prefs.userBranchSave,
            isComplete = bd.swAppHistoryComplete.isChecked,
            pic = ""
        )

        val pelindoAppsSelected = bd.atAppHistoryAppsName.text.toString()
        val index = pelindoAppListString.indexOf(pelindoAppsSelected)
        if (index == -1) {
            showToast("Harap memilih aplikasi", true)
            return
        }
        val appID = pelindoAppListIDString[index]

        if (args.isValid()) {
            viewModel.appendHistory(appID, args)
        } else {
            showToast(ERR_FORM_NOT_VALID, true)
        }
    }


    private fun observeViewModel() {

        viewModel.run {
            isHistoryCreated.observe(this@AppendPelindoAppsHistoryActivity, {
                killActivityIfHistoryCreated(it)
            })
            getAppsData().observe(this@AppendPelindoAppsHistoryActivity, {
                pelindoAppListString.clear()
                pelindoAppListIDString.clear()
                pelindoAppsListObject = it

                for (App in pelindoAppsListObject.apps) {
                    pelindoAppListString.add(App.appsName)
                    pelindoAppListIDString.add(App.id)
                }

                setAutoTextApps(pelindoAppListString)
            })
            isLoading.observe(this@AppendPelindoAppsHistoryActivity, { showLoading(it) })
            messageError.observe(this@AppendPelindoAppsHistoryActivity, { showToast(it, true) })
            messageSuccess.observe(this@AppendPelindoAppsHistoryActivity, { showToast(it, false) })
        }
    }

    private fun killActivityIfHistoryCreated(isCreated: Boolean) {
        if (isCreated) {
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