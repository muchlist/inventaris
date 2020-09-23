package com.muchlis.inventaris.view_model.pelindo_app_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindAppHistoryDto
import com.muchlis.inventaris.data.response.HistoryAppsListResponse
import com.muchlis.inventaris.repository.PelindoAppsRepo

class PelindoAppsHistoryViewModel : ViewModel() {

    //Data untuk RecyclerView
    private val _historyData: MutableLiveData<HistoryAppsListResponse> = MutableLiveData()
    fun getHistoryData(): MutableLiveData<HistoryAppsListResponse> {
        return _historyData
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

    private val _isHistoryDeleted = MutableLiveData<Boolean>()
    val isHistoryDeleted: LiveData<Boolean>
        get() = _isHistoryDeleted

    init {
        _isLoading.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun findHistories(data: FindAppHistoryDto) {
        _isLoading.value = true
        _messageError.value = ""
        PelindoAppsRepo.findPelindoAppsHistory(data) { response, error ->
            if (error.isNotEmpty()) {
                //Ada pesan error
                _isLoading.value = false
                _messageError.value = error
                return@findPelindoAppsHistory
            }
            response.let {
                _isLoading.value = false
                _historyData.postValue(response)
            }
        }
    }

    fun deleteHistoryFromServer(historyID: String) {
        _isLoading.value = true
        _messageError.value = ""
        _messageSuccess.value = ""
        PelindoAppsRepo.deletePelindoAppsHistory(historyID = historyID) { success, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@deletePelindoAppsHistory
            }
            if (success.isNotEmpty()) {
                _isLoading.value = false
                _messageSuccess.value = "Berhasil menghapus history"
                _isHistoryDeleted.value = true
            }
        }
    }
}