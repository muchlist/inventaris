package com.muchlis.inventaris.view_model.computer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.ComputerEditRequest
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.repository.ComputerRepo

class EditComputerViewModel : ViewModel() {

    private val _computerData: MutableLiveData<ComputerDetailResponse> = MutableLiveData()
    fun getComputerData(): MutableLiveData<ComputerDetailResponse> {
        return _computerData
    }

    fun setComputerData(data: ComputerDetailResponse) {
        _computerData.postValue(data)
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isComputerEdited = MutableLiveData<Boolean>()
    val isComputerEdited: LiveData<Boolean>
        get() = _isComputerEdited

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isComputerEdited.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun editComputerFromServer(args: ComputerEditRequest) {
        _isLoading.value = true
        _isComputerEdited.value = false
        ComputerRepo.editComputer(
            computerID = _computerData.value?.id ?: "",
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@editComputer
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Merubah komputer berhasil"
                _isComputerEdited.value = true
            }
        }
    }
}