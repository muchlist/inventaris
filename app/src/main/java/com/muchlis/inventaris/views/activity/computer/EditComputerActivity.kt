package com.muchlis.inventaris.views.activity.computer

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.ComputerEditRequest
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityEditComputerBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.computer.EditComputerViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class EditComputerActivity : AppCompatActivity() {

    private lateinit var viewModel: EditComputerViewModel
    private lateinit var bd: ActivityEditComputerBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    private lateinit var dateTimeNowCalander: Calendar
    private lateinit var dateTimeNow: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityEditComputerBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(EditComputerViewModel::class.java)

        //GET DATA FROM ANOTHER ACTIVITY
        val dataFromIntent: ComputerDetailResponse = intent.getParcelableExtra(INTENT_TO_EDIT_COMPUTER) as ComputerDetailResponse
        viewModel.setComputerData(dataFromIntent)

        validateJsonStringInSharedPrefsForDropdown()

        //Init date
        dateTimeNowCalander = Calendar.getInstance()

        setAllFormValue(dataFromIntent)
        setAllDropdownForAutoText()

        //CLICK HANDLE
        bd.btSave.setOnClickListener {
            createComputer()
        }

        bd.etComputerYear.setOnClickListener {
            showDatePicker()
        }
        bd.etfComputerYear.setOnClickListener {
            showDatePicker()
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
            android.R.layout.simple_spinner_dropdown_item,
            stringDropDown
        )
        view.setAdapter(adapter)
    }

    private fun setAllFormValue(data: ComputerDetailResponse){
        bd.etfComputerName.editText?.setText(data.clientName)
        bd.etfHostComputerName.editText?.setText(data.hostname)
        bd.etfComputerIpAddress.editText?.setText(data.ipAddress)
        bd.etfInventarisComputerName.editText?.setText(data.inventoryNumber)
        bd.etfComputerBranch.editText?.setText(App.prefs.userBranchSave)
        bd.atComputerDivisi.setText(data.division)
        bd.atComputerLocation.setText(data.location)
        bd.atComputerTipe.setText(data.tipe)
        bd.etfComputerMerk.editText?.setText(data.merk)
        bd.etComputerYear.setText(data.year.toDate().toStringJustDate())
        bd.atComputerSeatManajemen.setText(if (data.seatManagement) "YA" else "TIDAK")
        bd.etfNote.editText?.setText(data.note)

        bd.atComputerOs.setText(data.operationSystem)
        bd.atComputerProcessor.setText(data.spec.processor)
        bd.atComputerRam.setText(data.spec.ram.toString())
        bd.atComputerHardisk.setText(data.spec.hardisk.toString())
    }

    private fun observeViewModel() {
        viewModel.run {
            isComputerEdited.observe(this@EditComputerActivity, androidx.lifecycle.Observer {
                killActivityIfComputerCreated(it)
            })
            isLoading.observe(this@EditComputerActivity, androidx.lifecycle.Observer { showLoading(it) })
            messageError.observe(this@EditComputerActivity, androidx.lifecycle.Observer { showToast(it, true) })
            messageSuccess.observe(this@EditComputerActivity, androidx.lifecycle.Observer { showToast(it, false) })
        }
    }

    private fun createComputer() {
        formValidation { data, error ->
            if (error.isNotEmpty()) {
                showToast(error, true)
                return@formValidation
            }
            data?.let {
                viewModel.editComputerFromServer(args = it)
            }
        }
    }

    private fun formValidation(callback: (data: ComputerEditRequest?, error: String) -> Unit) {
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

        if (location.isNotEmpty()) {
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
            year.fromStringJustDatetoDate().toStringInputDate()
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
            val data = ComputerEditRequest(
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
                deactive = false,
                timestamp = viewModel.getComputerData().value?.updatedAt ?: ""
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
            App.fragmentDetailComputerMustBeRefresh = true
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