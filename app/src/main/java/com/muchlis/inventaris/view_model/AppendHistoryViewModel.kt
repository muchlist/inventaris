package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.repository.HistoryRepository
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppendHistoryViewModel : ViewModel() {
    private val historyRepo = HistoryRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isHistoryCreated = MutableLiveData<Boolean>()
    val isHistoryCreated: LiveData<Boolean>
        get() = _isHistoryCreated

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isHistoryCreated.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun appendHistory(parentID : String, args: HistoryRequest) {
        _isLoading.value = true
        _isHistoryCreated.value = false
        historyRepo.createHistory(parentID = parentID,
            args = args){
            response, error ->
            if (error.isNotEmpty()){
                _messageError.value = error
                return@createHistory
            }
            response.let {
                _messageSuccess.value = "Menambahkan riwayat berhasil"
                _isHistoryCreated.value = true
            }
        }
        _isLoading.value = false
    }
}