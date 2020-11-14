package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindHistoryDto
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.data.response.ProblemCountResponse
import com.muchlis.inventaris.repository.HistoryRepo
import com.muchlis.inventaris.repository.OptionSelectorRepo
import com.muchlis.inventaris.utils.App

class DashboardViewModel : ViewModel() {

    //Data untuk RecyclerView
    private val _historyData: MutableLiveData<HistoryListResponse> = MutableLiveData()
    fun getHistoryData(): MutableLiveData<HistoryListResponse> {
        return _historyData
    }

    //Data untuk chip group
    private val _historyCount: MutableLiveData<ProblemCountResponse> = MutableLiveData()
    fun getHistoryCountData(): MutableLiveData<ProblemCountResponse> {
        return _historyCount
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _isHistoryDeleted = MutableLiveData<Boolean>()
    val isHistoryDeleted: LiveData<Boolean>
        get() = _isHistoryDeleted

    init {
        _isLoading.value = false
        _messageError.value = ""
    }

    fun findHistories(data: FindHistoryDto) {
        _isLoading.value = true
        _messageError.value = ""
        HistoryRepo.getHistories(data) { response, error ->
            if (error.isNotEmpty()) {
                //Ada pesan error
                _isLoading.value = false
                _messageError.value = error
                return@getHistories
            }
            response.let {
                _isLoading.value = false
                _historyData.postValue(response)
            }
        }
    }

    fun getHistoriesCountFromServer() {
        _messageError.value = ""
        HistoryRepo.getHistoriesCount { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@getHistoriesCount
            }
            response.let {
                _historyCount.postValue(response)
            }
        }
    }


    fun deleteHistoryFromServer(historyID: String) {
        _isLoading.value = true
        _messageError.value = ""
        HistoryRepo.deleteHistory(historyID = historyID) { success, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@deleteHistory
            }
            if (success.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = "Berhasil menghapus history"
                _isHistoryDeleted.value = true
            }
        }
    }

    fun getOption() {
        OptionSelectorRepo.getOptions { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@getOptions
            }
            response.let {
                App.prefs.optionsJson = it
            }
        }
    }
}