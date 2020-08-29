package com.muchlis.inventaris.views.activity.cctv

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.CctvRequest
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityAppendCctvBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.cctv.AppendCctvViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class AppendCctvActivity : AppCompatActivity() {

    private lateinit var viewModel: AppendCctvViewModel
    private lateinit var bd: ActivityAppendCctvBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    private lateinit var dateTimeNowCalander: Calendar
    private lateinit var dateTimeNow: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityAppendCctvBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(AppendCctvViewModel::class.java)

        validateJsonStringInSharedPrefsForDropdown()

        //Init date
        dateTimeNowCalander = Calendar.getInstance()

        setAllDropdownForAutoText()

        //CLICK HANDLE
        bd.btSave.setOnClickListener {
            createCctv()
        }

        bd.btSaveContinue.setOnClickListener {
            createCctv(continueAfterSave = true)
        }

        bd.etCctvYear.setOnClickListener {
            showDatePicker()
        }
        bd.etfCctvYear.setOnClickListener {
            showDatePicker()
        }

        bd.ivBackButton.setOnClickListener {
            onBackPressed()
        }

        observeViewModel()

    }

    private fun validateJsonStringInSharedPrefsForDropdown() {
        if (JsonMarshaller().getOption() != null) {
            optionJsonObject = JsonMarshaller().getOption()!!
        } else {
            Toasty.error(this, ERR_DROPDOWN_NOT_LOAD, Toasty.LENGTH_LONG).show()
        }
    }

    private fun setAllDropdownForAutoText() {
        setAutoTextForm(
            view = bd.atCctvLocation,
            stringDropDown = optionJsonObject.locations.filter { s ->
                s.contains(App.prefs.userBranchSave) || s.contains(
                    "LAINNYA"
                )
            }
        )

        setAutoTextForm(
            view = bd.atCctvType,
            stringDropDown = optionJsonObject.cctvDevicesType
        )
    }

    private fun setAutoTextForm(view: AutoCompleteTextView, stringDropDown: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            stringDropDown
        )
        view.setAdapter(adapter)
    }

    private fun observeViewModel() {
        viewModel.run {
            isCctvCreatedAndFinish.observe(this@AppendCctvActivity, androidx.lifecycle.Observer {
                killActivityIfCctvCreated(it)
            })

            isLoading.observe(
                this@AppendCctvActivity,
                androidx.lifecycle.Observer { showLoading(it) })

            messageError.observe(
                this@AppendCctvActivity,
                androidx.lifecycle.Observer { showToast(it, true) })

            messageSuccess.observe(
                this@AppendCctvActivity,
                androidx.lifecycle.Observer {
                    showToast(it, false)
                })

            cctvCreatedAndContinue.observe(
                this@AppendCctvActivity,
                androidx.lifecycle.Observer { showMessageCreated(it) })
        }
    }

    private fun showMessageCreated(message: String) {
        if (message.isNotEmpty()) {
            bd.tvAppendStatus.visible()
            bd.tvAppendStatus.text = message

            bd.etfCctvName.editText?.setText("")
            App.activityCctvListMustBeRefresh = true
        } else {
            bd.tvAppendStatus.invisible()
        }
    }

    private fun createCctv(continueAfterSave: Boolean = false) {
        formValidation { data, error ->
            if (error.isNotEmpty()) {
                showToast(error, true)
                return@formValidation
            }
            data?.let {
                viewModel.appendCctv(args = it, isContinue = continueAfterSave)
            }
        }
    }

    private fun formValidation(callback: (data: CctvRequest?, error: String) -> Unit) {
        var error = 0

        val cctvName = bd.etfCctvName.editText?.text.toString()
        val location = bd.atCctvLocation.text.toString()
        var ipAddress = bd.etfCctvIpAddress.editText?.text.toString()
        var year = bd.etfCctvYear.editText?.text.toString()
        val tipe = bd.atCctvType.text.toString()

        if (cctvName.isEmpty()) {
            bd.etfCctvName.error = "Nama pengguna tidak boleh kosong!"
            error++
        }

        if (location.isEmpty()) {
            bd.containerCctvLocation.error = "Lokasi tidak boleh kosong!"
            error++
        } else {
            if (location !in optionJsonObject.locations) {
                bd.containerCctvLocation.error = "Lokasi salah!"
                error++
            }
        }

        if (tipe.isEmpty()) {
            bd.containerCctvLocation.error = "Tipe tidak boleh kosong!"
            error++
        }

        if (ipAddress.isEmpty()) {
            ipAddress = "0.0.0.0"
        } else {
            if (!Validation().isIPAddressValid(ipAddress)) {
                bd.etfCctvIpAddress.error = "Format IP Address Salah!"
                error++
            }
        }

        year = if (year.isEmpty()) {
            dateTimeNowCalander.time.toStringInputDate()
        } else {
            year.fromStringJustDatetoDate().toStringInputDate()
        }


        if (error > 0) {
            callback(null, "Form tidak valid")
        } else {
            val data = CctvRequest(
                cctvName = cctvName,
                inventoryNumber = bd.etfInventNumCctv.editText?.text.toString(),
                ipAddress = ipAddress,
                location = location,
                merk = bd.etfMerkCctv.editText?.text.toString(),
                year = year,
                note = bd.etfNote.editText?.text.toString(),
                tipe = tipe,
                deactive = false
            )

            callback(
                data, ""
            )
        }

    }


    private fun showDatePicker() {

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                //EXEKUSI DISINI JIKA TANGGAL DIGANTI
                dateTimeNowCalander.set(Calendar.YEAR, year)
                dateTimeNowCalander.set(Calendar.MONTH, month)
                dateTimeNowCalander.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dateTimeNowCalander.set(Calendar.HOUR_OF_DAY, 0)
                dateTimeNowCalander.set(Calendar.MINUTE, 0)
                dateTimeNowCalander.set(Calendar.SECOND, 1)

                val date = dateTimeNowCalander.time.toStringJustDate()

                //SET TO DISPLAY
                bd.etfCctvYear.editText?.setText(date)

                dateTimeNow = dateTimeNowCalander.time
            },
            dateTimeNowCalander.get(Calendar.YEAR),
            dateTimeNowCalander.get(Calendar.MONTH),
            dateTimeNowCalander.get(
                Calendar.DAY_OF_MONTH
            )
        )

        datePicker.show()
    }

    private fun killActivityIfCctvCreated(isCreated: Boolean) {
        if (isCreated) {
            App.activityCctvListMustBeRefresh = true
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bd.btSave.invisible()
            bd.btSaveContinue.invisible()
        } else {
            bd.btSave.visible()
            bd.btSaveContinue.visible()
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