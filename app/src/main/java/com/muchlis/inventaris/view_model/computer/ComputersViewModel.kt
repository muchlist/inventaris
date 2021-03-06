package com.muchlis.inventaris.view_model.computer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindComputersDto
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.repository.ComputerRepo

class ComputersViewModel : ViewModel() {


    //Data untuk RecyclerView
    private val _computerData: MutableLiveData<ComputerListResponse> = MutableLiveData()
    fun getComputerData(): MutableLiveData<ComputerListResponse> {
        return _computerData
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


    fun findComputersFromServer(data: FindComputersDto) {
        _isLoading.value = true
        _messageError.value = ""

        ComputerRepo.findComputers(data) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@findComputers
            }
            response.let {
                _isLoading.value = false
                _computerData.postValue(it)
                _isLoading.value = false
            }
        }
    }

}