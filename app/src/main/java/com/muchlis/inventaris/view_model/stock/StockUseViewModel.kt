package com.muchlis.inventaris.view_model.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.StockUseRequest
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.repository.StockRepository

class StockUseViewModel : ViewModel() {
    private val stockRepo = StockRepository

    private lateinit var stockDetail:  StockDetailResponse
    fun retrieveStockDetail(): StockDetailResponse {
        return stockDetail
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isStockUseCreated = MutableLiveData<Boolean>()
    val isStockUseCreated: LiveData<Boolean>
        get() = _isStockUseCreated

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isStockUseCreated.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun useStockFromServer(parentID : String, args: StockUseRequest) {
        _isLoading.value = true
        _isStockUseCreated.value = false
        stockRepo.useStock(parentID = parentID,
            args = args){
                response, error ->
            if (error.isNotEmpty()){
                _isLoading.value = false
                _messageError.value = error
                return@useStock
            }
            response?.let {
                _isLoading.value = false
                stockDetail = it
                _messageSuccess.value = "Perubahan stok berhasil"
                _isStockUseCreated.value = true
            }
        }
    }
}