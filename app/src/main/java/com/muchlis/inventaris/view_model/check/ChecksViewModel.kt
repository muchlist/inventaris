package com.muchlis.inventaris.view_model.check

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.response.CheckListResponse
import com.muchlis.inventaris.repository.CheckRepo

class ChecksViewModel : ViewModel() {

    //Data untuk RecyclerView
    private val _checkData: MutableLiveData<CheckListResponse> = MutableLiveData()
    fun getCheckData(): MutableLiveData<CheckListResponse> {
        return _checkData
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess

    init {
        _isLoading.value = false
        _messageError.value = ""
    }


    fun findChecksFromServer() {
        _isLoading.value = true
        _messageError.value = ""

        CheckRepo.findCheck { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@findCheck
            }
            response.let {
                _isLoading.value = false
                _checkData.postValue(it)
                _isLoading.value = false
            }
        }
    }

}