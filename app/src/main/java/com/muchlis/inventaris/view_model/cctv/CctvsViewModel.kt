package com.muchlis.inventaris.view_model.cctv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindCctvDto
import com.muchlis.inventaris.data.response.CctvListResponse
import com.muchlis.inventaris.repository.CctvRepo

class CctvsViewModel : ViewModel() {


    //Data untuk RecyclerView
    private val _cctvData: MutableLiveData<CctvListResponse> = MutableLiveData()
    fun getCctvData(): MutableLiveData<CctvListResponse> {
        return _cctvData
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


    fun findCctvFromServer(data: FindCctvDto) {
        _isLoading.value = true
        _messageError.value = ""

        CctvRepo.findCctvs(data) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@findCctvs
            }
            response.let {
                _isLoading.value = false
                _cctvData.postValue(it)
                _isLoading.value = false
            }
        }
    }

}