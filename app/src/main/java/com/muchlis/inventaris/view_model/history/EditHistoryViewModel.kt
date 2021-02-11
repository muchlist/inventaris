package com.muchlis.inventaris.view_model.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.HistoryEditRequest
import com.muchlis.inventaris.repository.HistoryRepo

class EditHistoryViewModel : ViewModel() {

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

    fun editHistoryR(historyID: String, args: HistoryEditRequest) {
        if (!updateHistoryValid(args)){
            _messageError.value = "Resolve note harus di isi!"
            return
        }
        _isLoading.value = true
        _isHistoryEdited.value = false
        HistoryRepo.editHistory(
            historyID = historyID,
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@editHistory
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Case berhasil diupdate"
                _isHistoryEdited.value = true
            }
        }
    }

    private fun updateHistoryValid(args: HistoryEditRequest): Boolean {
        if (args.completeStatus == 2) {
            if (args.resolveNote.isEmpty()) {
                return false
            }
        }
        return true
    }
}