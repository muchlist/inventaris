package com.muchlis.inventaris.view_model.handheld

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.JustTimeStampRequest
import com.muchlis.inventaris.data.response.HandheldDetailResponse
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.repository.HandheldRepo
import com.muchlis.inventaris.repository.HistoryRepo

class HandheldDetailViewModel : ViewModel() {

    //Data untuk detail
    private val _handheldData: MutableLiveData<HandheldDetailResponse> = MutableLiveData()
    fun getHandheldData(): MutableLiveData<HandheldDetailResponse> {
        return _handheldData
    }

    //Data untuk RecyclerView
    private val _historyData: MutableLiveData<HistoryListResponse> = MutableLiveData()
    fun getHistoryData(): MutableLiveData<HistoryListResponse> {
        return _historyData
    }

    private var handheldID: String = ""
    fun setHandheldId(id: String) {
        handheldID = id
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

    private val _messageDeleteHandheldSuccess = MutableLiveData<String>()
    val messageDeleteHandheldSuccess: LiveData<String>
        get() = _messageDeleteHandheldSuccess

    private val _deleteHandheldSuccess = MutableLiveData<Boolean>()
    val isdeleteHandheldSuccess: LiveData<Boolean>
        get() = _deleteHandheldSuccess

    private val _deleteHistorySuccess = MutableLiveData<Boolean>()
    val isDeleteHistorySuccess: LiveData<Boolean>
        get() = _deleteHistorySuccess

    init {
        _isLoading.value = false
        _deleteHandheldSuccess.value = false
        _messageError.value = ""
        _messageDeleteHandheldSuccess.value = ""
    }


    fun getHandheldFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        HandheldRepo.getHandheld(handheldID = handheldID) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@getHandheld
            }
            response.let {
                _isLoading.value = false
                _handheldData.postValue(it)
            }
        }
    }


    fun deleteHandheldFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        HandheldRepo.deleteHandheld(handheldID = handheldID) { success, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@deleteHandheld
            }
            if (success.isNotEmpty()) {
                _isLoading.value = false
                _deleteHandheldSuccess.value = true
                _messageDeleteHandheldSuccess.value = success
            }
        }
    }

    fun findHistoriesFromServer() {
        _isLoading.value = true
        _messageHistoryError.value = ""

        HistoryRepo.findHistoriesForParent(parentID = handheldID) { response, error ->
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
        HistoryRepo.deleteHistory(historyID = historyID) { success, error ->
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

    private fun switchStatusHandheld(): String{
        return if (_handheldData.value?.deactive == true){
            "ACTIVE"
        } else {
            "DEACTIVE"
        }
    }

    fun changeHandheldStatusFromServer(){
        _isLoading.value = true
        val args = JustTimeStampRequest(
            timeStamp = _handheldData.value?.updatedAt ?: ""
        )
        HandheldRepo.changeStatusHandheld(handheldID = handheldID, statusActive = switchStatusHandheld(),args = args){
                response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@changeStatusHandheld
            }
            response?.let {
                _isLoading.value = false
                _handheldData.postValue(it)
            }
        }
    }

}