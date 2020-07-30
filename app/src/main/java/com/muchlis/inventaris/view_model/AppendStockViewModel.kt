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

    private val _isStockCreated = MutableLiveData<Boolean>()
    val isStockCreated: LiveData<Boolean>
        get() = _isStockCreated

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isStockCreated.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun appendStock(args: StockRequest) {
        _isLoading.value = true
        _isStockCreated.value = false
        stockRepo.createStock(
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@createStock
            }
            response.let {
                _messageSuccess.value = "Menambahkan stok berhasil"
                _isStockCreated.value = true
            }
        }
        _isLoading.value = false
    }
}