package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.ComputerRequest
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.repository.ComputerRepository

class AppendComputerViewModel : ViewModel() {
    private val computerRepo = ComputerRepository

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isComputerCreated = MutableLiveData<Boolean>()
    val isComputerCreated: LiveData<Boolean>
        get() = _isComputerCreated

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isComputerCreated.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun appendComputer(args: ComputerRequest) {
        _isLoading.value = true
        _isComputerCreated.value = false
        computerRepo.createComputer(
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@createComputer
            }
            response.let {
                _messageSuccess.value = "Menambahkan komputer berhasil"
                _isComputerCreated.value = true
            }
        }
        _isLoading.value = false
    }
}