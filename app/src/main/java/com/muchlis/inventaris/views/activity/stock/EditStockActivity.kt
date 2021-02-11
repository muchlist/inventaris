package com.muchlis.inventaris.views.activity.stock

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.StockEditRequest
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.databinding.ActivityEditStockBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.stock.EditStockViewModel
import es.dmoral.toasty.Toasty

class EditStockActivity : AppCompatActivity() {

    private lateinit var viewModel: EditStockViewModel
    private lateinit var bd: ActivityEditStockBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityEditStockBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(EditStockViewModel::class.java)

        //GET DATA FROM ANOTHER ACTIVITY
        val dataFromIntent: StockDetailResponse? = intent.getParcelableExtra(
            INTENT_TO_EDIT_STOCK
        ) as StockDetailResponse?

        dataFromIntent?.let {
            viewModel.setStockData(it)
        }

        validateJsonStringInSharedPrefsForDropdown()

        dataFromIntent?.let {
            setAllFormValue(it)
        }
        setAllDropdownForAutoText()

        //CLICK HANDLE
        bd.btSave.setOnClickListener {
            createComputer()
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
            view = bd.atStockCategory,
            stringDropDown = optionJsonObject.stockCategory
        )
        setAutoTextForm(
            view = bd.atStockLocation,
            stringDropDown = optionJsonObject.stockLocations.filter { s ->
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

    private fun setAllFormValue(data: StockDetailResponse) {
        bd.etfStockName.editText?.setText(data.stockName)
        bd.atStockCategory.setText(data.category)
        bd.atStockLocation.setText(data.location)
        bd.etfUnitStock.editText?.setText(data.unit)
        bd.etfThresholdStock.editText?.setText(data.threshold.toString())
        bd.etfNote.editText?.setText(data.note)
    }

    private fun observeViewModel() {
        viewModel.run {
            isStockEdited.observe(this@EditStockActivity, {
                killActivityIfStockEdited(it)
            })
            isLoading.observe(
                this@EditStockActivity,
                { showLoading(it) })
            messageError.observe(
                this@EditStockActivity,
                { showToast(it, true) })
            messageSuccess.observe(
                this@EditStockActivity,
                { showToast(it, false) })
        }
    }

    private fun createComputer() {
        formValidation { data, error ->
            if (error.isNotEmpty()) {
                showToast(error, true)
                return@formValidation
            }
            data?.let {
                viewModel.editStockFromServer(args = it)
            }
        }
    }

    private fun formValidation(callback: (data: StockEditRequest?, error: String) -> Unit) {
        var error = 0

        val stockName = bd.etfStockName.editText?.text.toString()
        val category = bd.atStockCategory.text.toString()
        val location = bd.atStockLocation.text.toString()
        val unit = bd.etfUnitStock.editText?.text.toString()
        val threshold = bd.etfThresholdStock.editText?.text.toString()

        var thresholdDouble = 0.0

        if (stockName.isEmpty()) {
            bd.etfStockName.error = "Nama stok tidak boleh kosong!"
            error++
        }

        if (category.isEmpty()) {
            bd.containerStockCategory.error = "Kategori harus dipilih!"
            error++
        }

        if (location.isEmpty()) {
            bd.containerStockLocation.error = "Lokasi tidak boleh kosong!"
            error++
        } else {
            if (location !in optionJsonObject.stockLocations) {
                bd.containerStockLocation.error = "Lokasi salah!"
                error++
            }
        }

        if (unit.isEmpty()) {
            bd.etfUnitStock.error = "Satuan tidak boleh kosong!"
            error++
        }

        if (threshold.isEmpty()) {
            bd.etfThresholdStock.error = "Ambang batas masih kosong!"
        } else {
            thresholdDouble = threshold.toDoubleOrNull() ?: 0.0
        }

        if (error > 0) {
            callback(null, "Form tidak valid")
        } else {
            val data = StockEditRequest(
                stockName = stockName,
                category = category,
                location = location,
                note = bd.etfNote.editText?.text.toString(),
                threshold = thresholdDouble,
                unit = unit,
                deactive = false,
                timestamp = viewModel.getStockData().value?.updatedAt ?: ""
            )

            callback(
                data, ""
            )
        }

    }


    private fun killActivityIfStockEdited(isCreated: Boolean) {
        if (isCreated) {
            App.fragmentDetailStockMustBeRefresh = true
            App.activityStockListMustBeRefresh = true
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