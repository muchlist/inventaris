package com.muchlis.inventaris.view_model.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.repository.HistoryRepository

class AppendHistoryViewModel : ViewModel() {
    private val historyRepo = HistoryRepository

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
                _isLoading.value = false
                _messageError.value = error
                return@createHistory
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Menambahkan riwayat berhasil"
                _isHistoryCreated.value = true
            }
        }
    }
}