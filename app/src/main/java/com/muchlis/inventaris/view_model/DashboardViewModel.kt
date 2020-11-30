package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.muchlis.inventaris.data.response.DashboardResponse
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.repository.HistoryRepo
import com.muchlis.inventaris.repository.OptionSelectorRepo
import com.muchlis.inventaris.utils.App

class DashboardViewModel : ViewModel() {

    //Data untuk Dashboard
    private val _dashboardData: MutableLiveData<DashboardResponse> = MutableLiveData()
    fun getDashboardData(): MutableLiveData<DashboardResponse> {
        return _dashboardData
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _isHistoryDeleted = MutableLiveData<Boolean>()
    val isHistoryDeleted: LiveData<Boolean>
        get() = _isHistoryDeleted

    init {
        _isLoading.value = false
        _messageError.value = ""
    }

    fun getDashboardHistoriesFromServer() {
        _isLoading.value = true
        _messageError.value = ""
        HistoryRepo.getDashboard() { response, error ->
            if (error.isNotEmpty()) {
                //Ada pesan error
                _isLoading.value = false
                _messageError.value = error
                return@getDashboard
            }
            response.let {
                _isLoading.value = false
                _dashboardData.postValue(response)

                //if response option lvl != sharedPref call get option
                if (App.prefs.optionsJsonVersion < response?.optionLvl ?: 0) {
                    getOption()
                }
            }
        }
    }

    fun deleteHistoryFromServer(historyID: String) {
        _isLoading.value = true
        _messageError.value = ""
        HistoryRepo.deleteHistory(historyID = historyID) { success, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@deleteHistory
            }
            if (success.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = "Berhasil menghapus history"
                _isHistoryDeleted.value = true
            }
        }
    }

    fun getOption() {
        OptionSelectorRepo.getOptions { response, error ->
            if (error.isNotEmpty()) {
                _messageError.value = error
                return@getOptions
            }
            response.let {
                var recentVersion = 0
                if (it.isNotEmpty()) {
                    recentVersion = try {
                        val options = Gson().fromJson(it, SelectOptionResponse::class.java)
                        options.version
                    } catch (e: JsonParseException) {
                        0
                    }
                }

                App.prefs.optionsJson = it
                App.prefs.optionsJsonVersion = recentVersion
            }
        }
    }
}