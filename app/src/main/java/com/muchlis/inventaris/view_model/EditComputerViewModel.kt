package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.ComputerEditRequest
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.repository.ComputerRepository

class EditComputerViewModel : ViewModel() {
    private val computerRepo = ComputerRepository

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
        computerRepo.editComputer(
            computerID = _computerData.value?.id ?: "",
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@editComputer
            }
            response.let {
                _messageSuccess.value = "Merubah komputer berhasil"
                _isComputerEdited.value = true
            }
        }
        _isLoading.value = false
    }
}