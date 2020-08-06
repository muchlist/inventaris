package com.muchlis.inventaris.view_model.cctv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.CctvRequest
import com.muchlis.inventaris.repository.CctvRepo

class AppendCctvViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isCctvCreatedAndFinish = MutableLiveData<Boolean>()
    val isCctvCreatedAndFinish: LiveData<Boolean>
        get() = _isCctvCreatedAndFinish

    private val _cctvCreatedAndContinue = MutableLiveData<String>()
    val cctvCreatedAndContinue: LiveData<String>
        get() = _cctvCreatedAndContinue

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isCctvCreatedAndFinish.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
        _cctvCreatedAndContinue.value = ""
    }

    fun appendCctv(args: CctvRequest, isContinue: Boolean) {
        _isLoading.value = true
        _isCctvCreatedAndFinish.value = false
        CctvRepo.createCctv(
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@createCctv
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Menambahkan cctv berhasil"
                if (isContinue) {
                    _cctvCreatedAndContinue.value =
                        _cctvCreatedAndContinue.value + it?.cctvName + " Ditambahkan\n"
                } else {
                    _isCctvCreatedAndFinish.value = true
                }
            }
        }
    }
}