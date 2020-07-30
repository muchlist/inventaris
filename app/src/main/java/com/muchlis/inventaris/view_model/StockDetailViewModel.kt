package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.JustTimeStampRequest
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.repository.StockRepository
import com.muchlis.inventaris.utils.toStringView
import java.math.BigDecimal
import java.math.RoundingMode

class StockDetailViewModel : ViewModel() {

    private val stockRepo = StockRepository

    //Data untuk detail
    private val _stockData: MutableLiveData<StockDetailResponse> = MutableLiveData()
    fun getStockData(): MutableLiveData<StockDetailResponse> {
        return _stockData
    }
    fun setStockData(data: StockDetailResponse){
        _stockData.value = data
    }

    private var stockID: String = ""
    fun setStockId(id: String) {
        stockID = id
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageDeleteStockSuccess = MutableLiveData<String>()
    val messageDeleteStockSuccess: LiveData<String>
        get() = _messageDeleteStockSuccess

    private val _deleteStockSuccess = MutableLiveData<Boolean>()
    val isdeleteStockSuccess: LiveData<Boolean>
        get() = _deleteStockSuccess


    init {
        _isLoading.value = false
        _deleteStockSuccess.value = false
        _messageError.value = ""
        _messageDeleteStockSuccess.value = ""
    }


    fun getStockFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        stockRepo.getStock(computerID = stockID) { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@getStock
            }
            response.let {
                _stockData.postValue(it)
            }
        }

        _isLoading.value = false
    }

    fun deleteStockFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        stockRepo.deleteStock(stockID = stockID) { success, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@deleteStock
            }
            if (success.isNotEmpty()) {
                _deleteStockSuccess.value = true
                _messageDeleteStockSuccess.value = success
            }
        }
        _isLoading.value = false
    }

    private fun switchStatusComputer(): String{
        return if (_stockData.value?.deactive == true){
            "ACTIVE"
        } else {
            "DEACTIVE"
        }
    }

    fun changeStockStatusFromServer(){
        _isLoading.value = true
        val args = JustTimeStampRequest(
            timeStamp = _stockData.value?.updatedAt ?: ""
        )
        stockRepo.changeStatusStock(stockID = stockID, statusActive = switchStatusComputer(),args = args){
                response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@changeStatusStock
            }
            response?.let {
                _stockData.postValue(it)
            }
        }
        _isLoading.value = false
    }

    fun getTotalIncrement(): String{
        var total = 0.0
        val listIncrement = _stockData.value?.increment ?: emptyList()
        for (inc in listIncrement){
            total += inc.qty
        }

        return total.toStringView()
    }


    fun getTotalDecrement(): String{
        var total = 0.0
        val listIncrement = _stockData.value?.decrement ?: emptyList()
        for (inc in listIncrement){
            total += inc.qty
        }

        return total.toStringView()
    }

}