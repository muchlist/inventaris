package com.muchlis.inventaris.view_model.handheld

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindHandheldDto
import com.muchlis.inventaris.data.response.HandheldListResponse
import com.muchlis.inventaris.repository.HandheldRepo

class HandheldsViewModel : ViewModel() {


    //Data untuk RecyclerView
    private val _handheldData: MutableLiveData<HandheldListResponse> = MutableLiveData()
    fun getHandheldData(): MutableLiveData<HandheldListResponse> {
        return _handheldData
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


    fun findHandheldsFromServer(data: FindHandheldDto) {
        _isLoading.value = true
        _messageError.value = ""

        HandheldRepo.findHandhelds(data) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@findHandhelds
            }
            response.let {
                _isLoading.value = false
                _handheldData.postValue(it)
                _isLoading.value = false
            }
        }
    }

}