package com.muchlis.inventaris.views.activity.computer

import android.R.layout.simple_spinner_dropdown_item
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.ComputerRequest
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityAppendComputerBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.computer.AppendComputerViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class AppendComputerActivity : AppCompatActivity() {

    private lateinit var viewModel: AppendComputerViewModel
    private lateinit var bd: ActivityAppendComputerBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    private lateinit var dateTimeNowCalander: Calendar
    private lateinit var dateTimeNow: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityAppendComputerBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(AppendComputerViewModel::class.java)

        bd.etfComputerBranch.editText?.setText(App.prefs.userBranchSave)

        validateJsonStringInSharedPrefsForDropdown()

        //Init date
        dateTimeNowCalander = Calendar.getInstance()

        setAllDropdownForAutoText()

        //CLICK HANDLE
        bd.btSave.setOnClickListener {
            createComputer()
        }

        bd.btSaveContinue.setOnClickListener {
            createComputer(continueAfterSave = true)
        }

        bd.etComputerYear.setOnClickListener {
            showDatePicker()
        }
        bd.etfComputerYear.setOnClickListener {
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
            view = bd.atComputerDivisi,
            stringDropDown = optionJsonObject.divisions
        )
        setAutoTextForm(
            view = bd.atComputerHardisk,
            stringDropDown = optionJsonObject.hardisks.map { it.toString() }
        )

        setAutoTextForm(
            view = bd.atComputerLocation,
            stringDropDown = optionJsonObject.locations.filter { s ->
                s.contains(App.prefs.userBranchSave) || s.contains(
                    "LAINNYA"
                )
            }
        )
        setAutoTextForm(
            view = bd.atComputerOs,
            stringDropDown = optionJsonObject.operationSystem
        )
        setAutoTextForm(
            view = bd.atComputerProcessor,
            stringDropDown = optionJsonObject.processors
        )
        setAutoTextForm(
            view = bd.atComputerRam,
            stringDropDown = optionJsonObject.rams.map { it.toString() }
        )
        setAutoTextForm(
            view = bd.atComputerTipe,
            stringDropDown = optionJsonObject.pcDevicesType
        )
        setAutoTextForm(
            view = bd.atComputerSeatManajemen,
            stringDropDown = listOf("YA", "TIDAK")
        )
    }

    private fun setAutoTextForm(view: AutoCompleteTextView, stringDropDown: List<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            simple_spinner_dropdown_item,
            stringDropDown
        )
        view.setAdapter(adapter)
    }

    private fun observeViewModel() {
        viewModel.run {
            isComputerCreatedAndFinish.observe(this@AppendComputerActivity, Observer {
                killActivityIfComputerCreated(it)
            })
            isLoading.observe(this@AppendComputerActivity, Observer { showLoading(it) })
            messageError.observe(this@AppendComputerActivity, Observer { showToast(it, true) })
            messageSuccess.observe(this@AppendComputerActivity, Observer { showToast(it, false) })
            computerCreatedAndContinue.observe(
                this@AppendComputerActivity,
                Observer { showMessageCreated(it) })
        }
    }

    private fun showMessageCreated(message: String) {
        if (message.isNotEmpty()) {
            bd.tvAppendStatus.visible()
            bd.tvAppendStatus.text = message

            bd.etfComputerName.editText?.setText("")
            App.activityComputerListMustBeRefresh = true
        } else {
            bd.tvAppendStatus.invisible()
        }
    }

    private fun createComputer(continueAfterSave: Boolean = false) {
        formValidation { data, error ->
            if (error.isNotEmpty()) {
                showToast(error, true)
                return@formValidation
            }
            data?.let {
                viewModel.appendComputer(args = it, isContinue = continueAfterSave)
            }
        }
    }

    private fun formValidation(callback: (data: ComputerRequest?, error: String) -> Unit) {
        var error = 0

        val clientName = bd.etfComputerName.editText?.text.toString()
        val division = bd.atComputerDivisi.text.toString()
        val location = bd.atComputerLocation.text.toString()
        var ipAddress = bd.etfComputerIpAddress.editText?.text.toString()
        var year = bd.etfComputerYear.editText?.text.toString()
        var operationSystem = bd.atComputerOs.text.toString()
        val hardisk = bd.atComputerHardisk.text.toString()
        var processor = bd.atComputerProcessor.text.toString()
        val ram = bd.atComputerRam.text.toString()
        var tipe = bd.atComputerTipe.text.toString()

        var hardiskInt = 0
        var ramInt = 0

        if (clientName.isEmpty()) {
            bd.etfComputerName.error = "Nama pengguna tidak boleh kosong!"
            error++
        }

        if (division.isEmpty()) {
            bd.containerComputerDivisi.error = "Divisi harus dipilih!"
            error++
        }

        if (location.isEmpty()) {
            bd.containerComputerLocation.error = "Lokasi tidak boleh kosong!"
            error++
        } else {
            if (location !in optionJsonObject.locations) {
                bd.containerComputerLocation.error = "Lokasi salah!"
                error++
            }
        }

        if (ipAddress.isEmpty()) {
            ipAddress = "0.0.0.0"
        } else {
            if (!Validation().isIPAddressValid(ipAddress)) {
                bd.etfComputerIpAddress.error = "Format IP Address Salah!"
                error++
            }
        }

        year = if (year.isEmpty()) {
            dateTimeNowCalander.time.toStringInputDate()
        } else {
            year.fromddMMMyyyytoDate().toStringInputDate()
        }

        if (operationSystem.isEmpty()) {
            operationSystem = "LAINNYA"
        }

        if (hardisk.isEmpty()) {
            bd.containerComputerHardisk.error = "Hardisk masih kosong!"
        } else {
            hardiskInt = hardisk.toInt()
        }

        if (ram.isEmpty()) {
            bd.containerComputerRam.error = "Ram masih kosong!"
        } else {
            ramInt = ram.toInt()
        }

        if (processor.isEmpty()) {
            processor = "LAINNYA"
        }

        if (tipe.isEmpty()) {
            tipe = "LAINNYA"
        }

        if (error > 0) {
            callback(null, "Form tidak valid")
        } else {
            val data = ComputerRequest(
                clientName = clientName,
                division = division,
                hostname = bd.etfHostComputerName.editText?.text.toString(),
                inventoryNumber = bd.etfInventarisComputerName.editText?.text.toString(),
                ipAddress = ipAddress,
                location = location,
                merk = bd.etfComputerMerk.editText?.text.toString(),
                year = year,
                note = bd.etfNote.editText?.text.toString(),
                operationSystem = operationSystem,
                seatManagement = bd.atComputerSeatManajemen.text.toString() == "YA",
                hardisk = hardiskInt,
                processor = processor,
                ram = ramInt,
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

                val date = dateTimeNowCalander.time.toStringddMMMyyyy()

                //SET TO DISPLAY
                bd.etfComputerYear.editText?.setText(date)

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

    private fun killActivityIfComputerCreated(isCreated: Boolean) {
        if (isCreated) {
            App.activityComputerListMustBeRefresh = true
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