package com.muchlis.inventaris.view_model.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.StockEditRequest
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.repository.StockRepository

class EditStockViewModel : ViewModel() {
    private val stockRepo = StockRepository

    private val _stockData: MutableLiveData<StockDetailResponse> = MutableLiveData()
    fun getStockData(): MutableLiveData<StockDetailResponse> {
        return _stockData
    }

    fun setStockData(data: StockDetailResponse) {
        _stockData.postValue(data)
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isStockEdited = MutableLiveData<Boolean>()
    val isStockEdited: LiveData<Boolean>
        get() = _isStockEdited

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isStockEdited.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun editStockFromServer(args: StockEditRequest) {
        _isLoading.value = true
        _isStockEdited.value = false
        stockRepo.editStock(
            stockID = _stockData.value?.id ?: "",
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@editStock
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Merubah stok berhasil"
                _isStockEdited.value = true
            }
        }
    }
}