package com.muchlis.inventaris.views.activity.stock

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.muchlis.inventaris.data.request.StockRequest
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.ActivityAppendStockBinding
import com.muchlis.inventaris.utils.*
import com.muchlis.inventaris.view_model.AppendStockViewModel
import es.dmoral.toasty.Toasty

class AppendStockActivity : AppCompatActivity() {

    private lateinit var viewModel: AppendStockViewModel
    private lateinit var bd: ActivityAppendStockBinding

    private lateinit var optionJsonObject: SelectOptionResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityAppendStockBinding.inflate(layoutInflater)
        setContentView(bd.root)

        viewModel = ViewModelProvider(this).get(AppendStockViewModel::class.java)

        validateJsonStringInSharedPrefsForDropdown()

        setAllDropdownForAutoText()

        //CLICK HANDLE
        bd.btSave.setOnClickListener {
            createStock()
        }

        bd.btSaveContinue.setOnClickListener {
            createStock(continueAfterSave = true)
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

    private fun observeViewModel() {
        viewModel.run {
            isStockCreatedAndFinish.observe(this@AppendStockActivity, androidx.lifecycle.Observer {
                killActivityIfStockCreated(it)
            })
            isLoading.observe(
                this@AppendStockActivity,
                androidx.lifecycle.Observer { showLoading(it) })
            messageError.observe(
                this@AppendStockActivity,
                androidx.lifecycle.Observer { showToast(it, true) })
            messageSuccess.observe(
                this@AppendStockActivity,
                androidx.lifecycle.Observer { showToast(it, false) })
            stockCreatedContinue.observe(
                this@AppendStockActivity,
                Observer { showMessageCreated(it) })
        }
    }

    private fun showMessageCreated(message: String) {
        if (message.isNotEmpty()) {
            bd.tvAppendStatus.visible()
            bd.tvAppendStatus.text = message

            bd.etfStockName.editText?.setText("")
        } else {
            bd.tvAppendStatus.invisible()
        }
    }

    private fun createStock(continueAfterSave: Boolean = false) {
        formValidation { data, error ->
            if (error.isNotEmpty()) {
                showToast(error, true)
                return@formValidation
            }
            data?.let {
                viewModel.appendStock(args = it, isContinue = continueAfterSave)
            }
        }
    }

    private fun formValidation(callback: (data: StockRequest?, error: String) -> Unit) {
        var error = 0

        val stockName = bd.etfStockName.editText?.text.toString()
        val category = bd.atStockCategory.text.toString()
        val location = bd.atStockLocation.text.toString()
        val unit = bd.etfUnitStock.editText?.text.toString()
        val qty = bd.etfQtyStock.editText?.text.toString()
        val threshold = bd.etfThresholdStock.editText?.text.toString()

        var qtyDouble = 0.0
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

        if (qty.isEmpty()) {
            bd.etfQtyStock.error = "Jumlah awal masih kosong!"
        } else {
            qtyDouble = qty.toDoubleOrNull() ?: 0.0
        }

        if (threshold.isEmpty()) {
            bd.etfThresholdStock.error = "Ambang batas masih kosong!"
        } else {
            thresholdDouble = threshold.toDoubleOrNull() ?: 0.0
        }

        if (error > 0) {
            callback(null, "Form tidak valid")
        } else {
            val data = StockRequest(
                stockName = stockName,
                category = category,
                location = location,
                note = bd.etfNote.editText?.text.toString(),
                qty = qtyDouble,
                threshold = thresholdDouble,
                unit = unit,
                deactive = false
            )

            callback(
                data, ""
            )
        }

    }


    private fun killActivityIfStockCreated(isCreated: Boolean) {
        if (isCreated) {
            App.activityStockListMustBeRefresh = true
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