package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.StockRequest
import com.muchlis.inventaris.repository.StockRepository

class AppendStockViewModel : ViewModel() {
    private val stockRepo = StockRepository

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isStockCreatedAndFinish = MutableLiveData<Boolean>()
    val isStockCreatedAndFinish: LiveData<Boolean>
        get() = _isStockCreatedAndFinish

    private val _stockCreatedContinue = MutableLiveData<String>()
    val stockCreatedContinue: LiveData<String>
        get() = _stockCreatedContinue

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isStockCreatedAndFinish.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
        _stockCreatedContinue.value = ""
    }

    fun appendStock(args: StockRequest, isContinue: Boolean) {
        _isLoading.value = true
        _isStockCreatedAndFinish.value = false
        stockRepo.createStock(
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@createStock
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Menambahkan stok berhasil"
                if (isContinue){
                    _stockCreatedContinue.value = _stockCreatedContinue.value + it?.stockName + " Ditambahkan\n"
                } else {
                    _isStockCreatedAndFinish.value = true
                }
            }
        }
    }
}