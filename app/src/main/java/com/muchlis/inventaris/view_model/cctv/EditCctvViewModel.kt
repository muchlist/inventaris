package com.muchlis.inventaris.view_model.cctv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.CctvEditRequest
import com.muchlis.inventaris.data.response.CctvDetailResponse
import com.muchlis.inventaris.repository.CctvRepo

class EditCctvViewModel : ViewModel() {

    private val _cctvData: MutableLiveData<CctvDetailResponse> = MutableLiveData()
    fun getCctvData(): MutableLiveData<CctvDetailResponse> {
        return _cctvData
    }

    fun setCctvData(data: CctvDetailResponse) {
        _cctvData.postValue(data)
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isCctvEdited = MutableLiveData<Boolean>()
    val isCctvEdited: LiveData<Boolean>
        get() = _isCctvEdited

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isCctvEdited.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun editCctvFromServer(args: CctvEditRequest) {
        _isLoading.value = true
        _isCctvEdited.value = false
        CctvRepo.editCctv(
            cctvID = _cctvData.value?.id ?: "",
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@editCctv
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Merubah cctv berhasil"
                _isCctvEdited.value = true
            }
        }
    }
}