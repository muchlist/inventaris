package com.muchlis.inventaris.views.activity.handheld

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.HandheldEditRequest
import com.muchlis.inventaris.data.response.HandheldDetailResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityEditHandheldBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.handheld.EditHandheldViewModel
import es.dmoral.toasty.Toasty
import java.util.*

class EditHandheldActivity : AppCompatActivity() {

    private lateinit var viewModel: EditHandheldViewModel
    private lateinit var bd: ActivityEditHandheldBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    private lateinit var dateTimeNowCalander: Calendar
    private lateinit var dateTimeNow: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityEditHandheldBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(EditHandheldViewModel::class.java)

        //GET DATA FROM ANOTHER ACTIVITY
        val dataFromIntent: HandheldDetailResponse =
            intent.getParcelableExtra(INTENT_TO_EDIT_HH) as HandheldDetailResponse
        viewModel.setHandheldData(dataFromIntent)

        validateJsonStringInSharedPrefsForDropdown()

        //Init date
        dateTimeNowCalander = Calendar.getInstance()

        setAllFormValue(dataFromIntent)
        setAllDropdownForAutoText()

        //CLICK HANDLE
        bd.btSave.setOnClickListener {
            editHandheld()
        }

        bd.etHandheldYear.setOnClickListener {
            showDatePicker()
        }
        bd.etfHandheldYear.setOnClickListener {
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
            view = bd.atHandheldTipe,
            stringDropDown = listOf("TABLET", "IPAD", "SMARTPHONE", "LAINNYA")
        )

        setAutoTextForm(
            view = bd.atHandheldLocation,
            stringDropDown = optionJsonObject.locations.filter { s ->
                s.contains(App.prefs.userBranchSave) || s.contains(
                    "LAINNYA"
                )
            }
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

    private fun setAllFormValue(data: HandheldDetailResponse) {
        bd.etfHandheldName.editText?.setText(data.handheldName)
        bd.etfHandheldIpAddress.editText?.setText(data.ipAddress)
        bd.etfInventarisHandheldName.editText?.setText(data.inventoryNumber)
        bd.etfHandheldBranch.editText?.setText(App.prefs.userBranchSave)
        bd.etfInventarisHandheldPhoneNumber.editText?.setText(data.phone)
        bd.atHandheldLocation.setText(data.location)
        bd.atHandheldTipe.setText(data.tipe)
        bd.etfHandheldMerk.editText?.setText(data.merk)
        bd.etHandheldYear.setText(data.year.toDate().toStringddMMMyyyy())
        bd.etfNote.editText?.setText(data.note)
    }

    private fun observeViewModel() {
        viewModel.run {
            isHandheldEdited.observe(this@EditHandheldActivity, {
                killActivityIfHandheldCreated(it)
            })
            isLoading.observe(this@EditHandheldActivity, { showLoading(it) })
            messageError.observe(this@EditHandheldActivity, { showToast(it, true) })
            messageSuccess.observe(this@EditHandheldActivity, { showToast(it, false) })
        }
    }

    private fun editHandheld() {
        formValidation { data, error ->
            if (error.isNotEmpty()) {
                showToast(error, true)
                return@formValidation
            }
            data?.let {
                viewModel.editHandheldFromServer(args = it)
            }
        }
    }

    private fun formValidation(callback: (data: HandheldEditRequest?, error: String) -> Unit) {
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

        if (location.isEmpty()) {
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
            val data = HandheldEditRequest(
                handheldName = handheldName,
                inventoryNumber = bd.etfInventarisHandheldName.editText?.text.toString(),
                ipAddress = ipAddress,
                location = location,
                merk = bd.etfHandheldMerk.editText?.text.toString(),
                year = year,
                note = bd.etfNote.editText?.text.toString(),
                deactive = false,
                tipe = tipe,
                phone = bd.etfInventarisHandheldPhoneNumber.editText?.text.toString(),
                timestamp = viewModel.getHandheldData().value?.updatedAt ?: ""
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
            App.fragmentDetailHHMustBeRefresh = true
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