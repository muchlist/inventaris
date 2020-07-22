package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.repository.ComputerRepository
import com.muchlis.inventaris.repository.HistoryRepository

class ComputerDetailViewModel : ViewModel() {

    private val historyRepo = HistoryRepository
    private val computerRepo = ComputerRepository

    //Data untuk detail
    private val _computerData: MutableLiveData<ComputerDetailResponse> = MutableLiveData()
    fun getComputerData(): MutableLiveData<ComputerDetailResponse> {
        return _computerData
    }

    //Data untuk RecyclerView
    private val _historyData: MutableLiveData<HistoryListResponse> = MutableLiveData()
    fun getHistoryData(): MutableLiveData<HistoryListResponse> {
        return _historyData
    }

    private var computerID: String = ""
    fun setComputerId(id: String) {
        computerID = id
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


    fun getComputerFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        computerRepo.getComputer(computerID = computerID) { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@getComputer
            }
            response.let {
                _computerData.postValue(it)
            }
        }

        _isLoading.value = false
    }


    fun deleteComputerFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        computerRepo.deleteComputer(computerID = computerID) { success, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@deleteComputer
            }
            if (success.isNotEmpty()) {
                _deleteComputerSuccess.value = true
                _messageDeleteComputerSuccess.value = success
            }
        }
        _isLoading.value = false
    }

    fun findHistoriesFromServer() {
        _isLoading.value = true
        _messageHistoryError.value = ""

        historyRepo.findHistoriesForParent(parentID = computerID) { response, error ->
            if (error.isNotEmpty()) {
                _messageHistoryError.value = error
                return@findHistoriesForParent
            }
            response.let {
                _historyData.postValue(it)
            }
        }
        _isLoading.value = false
    }

    fun deleteHistoryFromServer(historyID: String) {
        _isLoading.value = true
        _messageHistoryError.value = ""
        historyRepo.deleteHistory(historyID = historyID) { success, error ->
            if (error.isNotEmpty()) {
                _messageHistoryError.value = error
                return@deleteHistory
            }
            if (success.isNotEmpty()) {
                _messageDeleteHistorySuccess.value = "Berhasil menghapus history"
                _deleteHistorySuccess.value = true
            }
        }
        _isLoading.value = false
    }

}