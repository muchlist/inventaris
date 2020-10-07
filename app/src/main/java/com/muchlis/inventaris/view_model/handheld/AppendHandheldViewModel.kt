package com.muchlis.inventaris.view_model.handheld

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.HandheldRequest
import com.muchlis.inventaris.repository.HandheldRepo

class AppendHandheldViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isHandheldCreatedAndFinish = MutableLiveData<Boolean>()
    val isHandheldCreatedAndFinish: LiveData<Boolean>
        get() = _isHandheldCreatedAndFinish

    private val _handheldCreatedAndContinue = MutableLiveData<String>()
    val handheldCreatedAndContinue: LiveData<String>
        get() = _handheldCreatedAndContinue

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isHandheldCreatedAndFinish.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
        _handheldCreatedAndContinue.value = ""
    }

    fun appendHandheld(args: HandheldRequest, isContinue: Boolean) {
        _isLoading.value = true
        _isHandheldCreatedAndFinish.value = false
        HandheldRepo.createHandheld(
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@createHandheld
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Menambahkan tablet berhasil"
                if (isContinue){
                    _handheldCreatedAndContinue.value = _handheldCreatedAndContinue.value + it?.handheldName + " Ditambahkan\n"
                } else {
                    _isHandheldCreatedAndFinish.value = true
                }
            }
        }
    }
}