package com.muchlis.inventaris.view_model.cctv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.JustTimeStampRequest
import com.muchlis.inventaris.data.response.CctvDetailResponse
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.repository.CctvRepository
import com.muchlis.inventaris.repository.HistoryRepository

class CctvDetailViewModel : ViewModel() {

    private val historyRepo = HistoryRepository
    private val cctvRepo = CctvRepository

    //Data untuk detail
    private val _cctvData: MutableLiveData<CctvDetailResponse> = MutableLiveData()
    fun getCctvData(): MutableLiveData<CctvDetailResponse> {
        return _cctvData
    }

    //Data untuk RecyclerView
    private val _historyData: MutableLiveData<HistoryListResponse> = MutableLiveData()
    fun getHistoryData(): MutableLiveData<HistoryListResponse> {
        return _historyData
    }

    private var cctvID: String = ""
    fun setCctvID(id: String) {
        cctvID = id
    }


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageHistoryError = MutableLiveData<String>()
    val messageHistoryError: LiveData<String>
        get() = _messageHistoryError

    private val _messageDeleteHistorySuccess = MutableLiveData<String>()
    val messageDeleteHistorySuccess: LiveData<String>
        get() = _messageDeleteHistorySuccess

    private val _messageDeleteComputerSuccess = MutableLiveData<String>()
    val messageDeleteComputerSuccess: LiveData<String>
        get() = _messageDeleteComputerSuccess

    private val _deleteComputerSuccess = MutableLiveData<Boolean>()
    val isdeleteComputerSuccess: LiveData<Boolean>
        get() = _deleteComputerSuccess

    private val _deleteHistorySuccess = MutableLiveData<Boolean>()
    val isDeleteHistorySuccess: LiveData<Boolean>
        get() = _deleteHistorySuccess

    init {
        _isLoading.value = false
        _deleteComputerSuccess.value = false
        _messageError.value = ""
        _messageDeleteComputerSuccess.value = ""
    }


    fun getCctvFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        cctvRepo.getCctv(cctvID = cctvID) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@getCctv
            }
            response.let {
                _isLoading.value = false
                _cctvData.postValue(it)
            }
        }
    }


    fun deleteCctvFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        cctvRepo.deleteCctv(cctvID = cctvID) { success, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@deleteCctv
            }
            if (success.isNotEmpty()) {
                _isLoading.value = false
                _deleteComputerSuccess.value = true
                _messageDeleteComputerSuccess.value = success
            }
        }
    }

    fun findHistoriesFromServer() {
        _isLoading.value = true
        _messageHistoryError.value = ""

        historyRepo.findHistoriesForParent(parentID = cctvID) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageHistoryError.value = error
                return@findHistoriesForParent
            }
            response.let {
                _isLoading.value = false
                _historyData.postValue(it)
            }
        }
    }

    fun deleteHistoryFromServer(historyID: String) {
        _isLoading.value = true
        _messageHistoryError.value = ""
        historyRepo.deleteHistory(historyID = historyID) { success, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageHistoryError.value = error
                return@deleteHistory
            }
            if (success.isNotEmpty()) {
                _isLoading.value = false
                _messageDeleteHistorySuccess.value = "Berhasil menghapus history"
                _deleteHistorySuccess.value = true
            }
        }
    }

    private fun switchStatusCctv(): String {
        return if (_cctvData.value?.deactive == true) {
            "ACTIVE"
        } else {
            "DEACTIVE"
        }
    }

    fun changeCctvStatusFromServer(){
        _isLoading.value = true
        val args = JustTimeStampRequest(
            timeStamp = _cctvData.value?.updatedAt ?: ""
        )
        cctvRepo.changeStatusCctv(cctvID = cctvID, statusActive = switchStatusCctv(),args = args){
                response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@changeStatusCctv
            }
            response?.let {
                _isLoading.value = false
                _cctvData.postValue(it)
            }
        }
    }

}