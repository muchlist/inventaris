package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindHistoryDto
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.repository.HistoryRepository
import com.muchlis.inventaris.repository.OptionSelectorRepository
import com.muchlis.inventaris.utils.App

class DashboardViewModel : ViewModel() {

    private val historyRepo = HistoryRepository()
    private val optionRepo = OptionSelectorRepository()

    //Data untuk RecyclerView
    private val _historyData: MutableLiveData<HistoryListResponse> = MutableLiveData()
    fun getHistoryData(): MutableLiveData<HistoryListResponse> {
        return _historyData
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    init {
        _isLoading.value = false
        _messageError.value = ""
    }

    fun findHistories(data: FindHistoryDto) {
        _isLoading.value = true
        _messageError.value = ""
        historyRepo.getHistories(data) { response, error ->
            if (error.isNotEmpty()) {
                //Ada pesan error
                _messageError.value = error
                return@getHistories
            }
            response.let {
                _historyData.postValue(response)
            }
        }
        _isLoading.value = false
    }

    fun getOption() {
        optionRepo.getOptions { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@getOptions
            }
            response.let {
                App.prefs.optionsJson = it
            }
        }
    }
}