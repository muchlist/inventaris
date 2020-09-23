package com.muchlis.inventaris.view_model.pelindo_app_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.HistoryAppsEditRequest
import com.muchlis.inventaris.repository.PelindoAppsRepo

class EditPelindoAppsHistoryViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isHistoryEdited = MutableLiveData<Boolean>()
    val isHistoryEdited: LiveData<Boolean>
        get() = _isHistoryEdited

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isHistoryEdited.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun editHistory(historyID: String, args: HistoryAppsEditRequest) {
        _isLoading.value = true
        _isHistoryEdited.value = false
        PelindoAppsRepo.editPelindoAppsHistory(
            historyID = historyID,
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@editPelindoAppsHistory
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Insiden berhasil diselesaikan"
                _isHistoryEdited.value = true
            }
        }
    }
}