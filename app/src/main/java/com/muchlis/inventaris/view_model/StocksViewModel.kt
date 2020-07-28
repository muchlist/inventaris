package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindStocksDto
import com.muchlis.inventaris.data.response.StockListResponse
import com.muchlis.inventaris.repository.StockRepository

class StocksViewModel : ViewModel() {

    private val stockRepo = StockRepository

    //Data untuk RecyclerView
    private val _stockData: MutableLiveData<StockListResponse> = MutableLiveData()
    fun getStockData(): MutableLiveData<StockListResponse> {
        return _stockData
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess

    init {
        _isLoading.value = false
        _messageError.value = ""
    }


    fun findStockFromServer(data: FindStocksDto) {
        _isLoading.value = true
        _messageError.value = ""

        stockRepo.findStocks(data) { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@findStocks
            }
            response.let {
                _stockData.postValue(it)
            }
        }
        _isLoading.value = false
    }

}