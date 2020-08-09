package com.muchlis.inventaris.views.activity.cctv

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.CctvEditRequest
import com.muchlis.inventaris.data.response.CctvDetailResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityEditCctvBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.cctv.EditCctvViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class EditCctvActivity : AppCompatActivity() {

    private lateinit var viewModel: EditCctvViewModel
    private lateinit var bd: ActivityEditCctvBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    private lateinit var dateTimeNowCalander: Calendar
    private lateinit var dateTimeNow: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityEditCctvBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(EditCctvViewModel::class.java)

        //GET DATA FROM ANOTHER ACTIVITY
        val dataFromIntent: CctvDetailResponse =
            intent.getParcelableExtra(INTENT_TO_EDIT_CCTV) as CctvDetailResponse
        viewModel.setCctvData(dataFromIntent)

        validateJsonStringInSharedPrefsForDropdown()

        //Init date
        dateTimeNowCalander = Calendar.getInstance()

        setAllFormValue(dataFromIntent)
        setAllDropdownForAutoText()

        //CLICK HANDLE
        bd.btSave.setOnClickListener {
            createCctv()
        }

        bd.etCctvYear.setOnClickListener {
            showDatePicker()
        }
        bd.etfCctvYear.setOnClickListener {
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

    private fun setAllFormValue(data: CctvDetailResponse) {
        bd.etfCctvName.editText?.setText(data.cctvName)
        bd.etfCctvIpAddress.editText?.setText(data.ipAddress)
        bd.etfInventNumCctv.editText?.setText(data.inventoryNumber)
        bd.atCctvLocation.setText(data.location)
        bd.atCctvType.setText(data.tipe)
        bd.etfMerkCctv.editText?.setText(data.merk)
        bd.etCctvYear.setText(data.year.toDate().toStringJustDate())
        bd.etfNote.editText?.setText(data.note)
    }

    private fun observeViewModel() {
        viewModel.run {
            isCctvEdited.observe(this@EditCctvActivity, androidx.lifecycle.Observer {
                killActivityIfCctvCreated(it)
            })
            isLoading.observe(
                this@EditCctvActivity,
                androidx.lifecycle.Observer { showLoading(it) })
            messageError.observe(
                this@EditCctvActivity,
                androidx.lifecycle.Observer { showToast(it, true) })
            messageSuccess.observe(
                this@EditCctvActivity,
                androidx.lifecycle.Observer { showToast(it, false) })
        }
    }

    private fun createCctv() {
        formValidation { data, error ->
            if (error.isNotEmpty()) {
                showToast(error, true)
                return@formValidation
            }
            data?.let {
                viewModel.editCctvFromServer(args = it)
            }
        }
    }

    private fun formValidation(callback: (data: CctvEditRequest?, error: String) -> Unit) {
        var error = 0

        val cctvName = bd.etfCctvName.editText?.text.toString()
        val location = bd.atCctvLocation.text.toString()
        var ipAddress = bd.etfCctvIpAddress.editText?.text.toString()
        var year = bd.etfCctvYear.editText?.text.toString()
        val tipe = bd.atCctvType.text.toString()

        if (cctvName.isEmpty()) {
            bd.etfCctvName.error = "Nama cctv tidak boleh kosong!"
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
            val data = CctvEditRequest(
                cctvName = cctvName,
                inventoryNumber = bd.etfInventNumCctv.editText?.text.toString(),
                ipAddress = ipAddress,
                location = location,
                merk = bd.etfMerkCctv.editText?.text.toString(),
                year = year,
                note = bd.etfNote.editText?.text.toString(),
                tipe = tipe,
                deactive = false,
                timestamp = viewModel.getCctvData().value?.updatedAt ?: ""
            )

            callback(
                data, ""
            )
        }
    }

    private fun showDatePicker() {

        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

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
            App.fragmentDetailCctvMustBeRefresh = true
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