package com.muchlis.inventaris.views.activity.handheld

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.HandheldRequest
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityAppendHandheldBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.handheld.AppendHandheldViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class AppendHandheldActivity : AppCompatActivity() {

    private lateinit var viewModel: AppendHandheldViewModel
    private lateinit var bd: ActivityAppendHandheldBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    private lateinit var dateTimeNowCalander: Calendar
    private lateinit var dateTimeNow: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityAppendHandheldBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(AppendHandheldViewModel::class.java)

        bd.etfHandheldBranch.editText?.setText(App.prefs.userBranchSave)

        validateJsonStringInSharedPrefsForDropdown()

        //Init date
        dateTimeNowCalander = Calendar.getInstance()

        setAllDropdownForAutoText()

        //CLICK HANDLE
        bd.btSave.setOnClickListener {
            createHandheld()
        }

        bd.btSaveContinue.setOnClickListener {
            createHandheld(continueAfterSave = true)
        }

        bd.etHandheldYear.setOnClickListener {
            showDatePicker()
        }
        bd.etfHandheldYear.setOnClickListener {
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
            view = bd.atHandheldLocation,
            stringDropDown = optionJsonObject.locations.filter { s ->
                s.contains(App.prefs.userBranchSave) || s.contains(
                    "LAINNYA"
                )
            }
        )

        setAutoTextForm(
            view = bd.atHandheldTipe,
            stringDropDown = listOf("TABLET", "IPAD", "SMARTPHONE", "LAINNYA")
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
            isHandheldCreatedAndFinish.observe(this@AppendHandheldActivity, {
                killActivityIfHandheldCreated(it)
            })
            isLoading.observe(this@AppendHandheldActivity, { showLoading(it) })
            messageError.observe(this@AppendHandheldActivity, { showToast(it, true) })
            messageSuccess.observe(this@AppendHandheldActivity, { showToast(it, false) })
            handheldCreatedAndContinue.observe(
                this@AppendHandheldActivity,
                { showMessageCreated(it) })
        }
    }

    private fun showMessageCreated(message: String) {
        if (message.isNotEmpty()) {
            bd.tvAppendStatus.visible()
            bd.tvAppendStatus.text = message

            bd.etfHandheldName.editText?.setText("")
            App.activityHHListMustBeRefresh = true
        } else {
            bd.tvAppendStatus.invisible()
        }
    }

    private fun createHandheld(continueAfterSave: Boolean = false) {
        formValidation { data, error ->
            if (error.isNotEmpty()) {
                showToast(error, true)
                return@formValidation
            }
            data?.let {
                viewModel.appendHandheld(args = it, isContinue = continueAfterSave)
            }
        }
    }

    private fun formValidation(callback: (data: HandheldRequest?, error: String) -> Unit) {
        var error = 0

        val handheldName = bd.etfHandheldName.editText?.text.toString()
        val location = bd.atHandheldLocation.text.toString()
        val tipe = bd.atHandheldTipe.text.toString()
        var ipAddress = bd.etfHandheldIpAddress.editText?.text.toString()
        var year = bd.etfHandheldYear.editText?.text.toString()

        if (handheldName.isEmpty()) {
            bd.etfHandheldName.error = "Nama pengguna tidak boleh kosong!"
            error++
        }

        if (tipe.isEmpty()) {
            bd.containerHandheldTipe.error = "Jenis perangkat tidak boleh kosong!"
            error++
        }

        if (location.isEmpty()) {
            bd.containerHandheldLocation.error = "Lokasi tidak boleh kosong!"
            error++
        } else {
            if (location !in optionJsonObject.locations) {
                bd.containerHandheldLocation.error = "Lokasi salah!"
                error++
            }
        }

        if (ipAddress.isEmpty()) {
            ipAddress = "0.0.0.0"
        } else {
            if (!Validation().isIPAddressValid(ipAddress)) {
                bd.etfHandheldIpAddress.error = "Format IP Address Salah!"
                error++
            }
        }

        year = if (year.isEmpty()) {
            dateTimeNowCalander.time.toStringInputDate()
        } else {
            year.fromddMMMyyyytoDate().toStringInputDate()
        }

        if (error > 0) {
            callback(null, "Form tidak valid")
        } else {
            val data = HandheldRequest(
                handheldName = handheldName,
                inventoryNumber = bd.etfInventarisHandheldName.editText?.text.toString(),
                ipAddress = ipAddress,
                location = location,
                merk = bd.etfHandheldMerk.editText?.text.toString(),
                year = year,
                note = bd.etfNote.editText?.text.toString(),
                deactive = false,
                tipe = tipe,
                phone = bd.etfInventarisHandheldPhoneNumber.editText?.text.toString()
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

                val date = dateTimeNowCalander.time.toStringddMMMyyyy()

                //SET TO DISPLAY
                bd.etfHandheldYear.editText?.setText(date)

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

    private fun killActivityIfHandheldCreated(isCreated: Boolean) {
        if (isCreated) {
            App.activityHHListMustBeRefresh = true
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