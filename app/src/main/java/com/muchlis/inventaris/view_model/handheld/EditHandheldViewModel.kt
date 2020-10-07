package com.muchlis.inventaris.view_model.handheld

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.HandheldEditRequest
import com.muchlis.inventaris.data.response.HandheldDetailResponse
import com.muchlis.inventaris.repository.HandheldRepo

class EditHandheldViewModel : ViewModel() {

    private val _handheldData: MutableLiveData<HandheldDetailResponse> = MutableLiveData()
    fun getHandheldData(): MutableLiveData<HandheldDetailResponse> {
        return _handheldData
    }

    fun setHandheldData(data: HandheldDetailResponse) {
        _handheldData.postValue(data)
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isHandheldEdited = MutableLiveData<Boolean>()
    val isHandheldEdited: LiveData<Boolean>
        get() = _isHandheldEdited

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isHandheldEdited.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun editHandheldFromServer(args: HandheldEditRequest) {
        _isLoading.value = true
        _isHandheldEdited.value = false
        HandheldRepo.editHandheld(
            handheldID = _handheldData.value?.id ?: "",
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@editHandheld
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Mengubah handheld berhasil"
                _isHandheldEdited.value = true
            }
        }
    }
}