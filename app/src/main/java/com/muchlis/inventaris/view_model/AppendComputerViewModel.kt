package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.ComputerRequest
import com.muchlis.inventaris.repository.ComputerRepository

class AppendComputerViewModel : ViewModel() {
    private val computerRepo = ComputerRepository

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isComputerCreatedAndFinish = MutableLiveData<Boolean>()
    val isComputerCreatedAndFinish: LiveData<Boolean>
        get() = _isComputerCreatedAndFinish

    private val _computerCreatedAndContinue = MutableLiveData<String>()
    val computerCreatedAndContinue: LiveData<String>
        get() = _computerCreatedAndContinue

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isComputerCreatedAndFinish.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
        _computerCreatedAndContinue.value = ""
    }

    fun appendComputer(args: ComputerRequest, isContinue: Boolean) {
        _isLoading.value = true
        _isComputerCreatedAndFinish.value = false
        computerRepo.createComputer(
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@createComputer
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Menambahkan komputer berhasil"
                if (isContinue){
                    _computerCreatedAndContinue.value = _computerCreatedAndContinue.value + it?.clientName + " Ditambahkan\n"
                } else {
                    _isComputerCreatedAndFinish.value = true
                }
            }
        }
    }
}