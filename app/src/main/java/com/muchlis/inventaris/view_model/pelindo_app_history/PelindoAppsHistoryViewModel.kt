package com.muchlis.inventaris.view_model.pelindo_app_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindAppHistoryDto
import com.muchlis.inventaris.data.response.HistoryAppsListResponse
import com.muchlis.inventaris.data.response.PelindoAppsListResponse
import com.muchlis.inventaris.repository.PelindoAppsRepo
import com.muchlis.inventaris.utils.TranslateMinuteToHour

class PelindoAppsHistoryViewModel : ViewModel() {

    //Data untuk RecyclerView
    private val _historyData: MutableLiveData<HistoryAppsListResponse> = MutableLiveData()
    fun getHistoryData(): MutableLiveData<HistoryAppsListResponse> {
        return _historyData
    }

    private lateinit var appsData: PelindoAppsListResponse

    private val _appsListName: MutableList<String> = mutableListOf()
    fun getAppsListName(): MutableList<String> {
        return _appsListName
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

    private val _count = MutableLiveData<String>()
    val getCount: LiveData<String>
        get() = _count

    private val _minute = MutableLiveData<String>()
    val getMinute: LiveData<String>
        get() = _minute

    private val _isHistoryDeleted = MutableLiveData<Boolean>()
    val isHistoryDeleted: LiveData<Boolean>
        get() = _isHistoryDeleted

    init {
        _isLoading.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun findHistories(data: FindAppHistoryDto) {
        _isLoading.value = true
        _messageError.value = ""
        PelindoAppsRepo.findPelindoAppsHistory(data) { response, error ->
            if (error.isNotEmpty()) {
                //Ada pesan error
                _isLoading.value = false
                _messageError.value = error
                return@findPelindoAppsHistory
            }
            response?.let {
                _isLoading.value = false
                _historyData.postValue(response)
                setCountAndMinute(response)
            }
        }
    }

    fun deleteHistoryFromServer(historyID: String) {
        _isLoading.value = true
        _messageError.value = ""
        _messageSuccess.value = ""
        PelindoAppsRepo.deletePelindoAppsHistory(historyID = historyID) { success, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@deletePelindoAppsHistory
            }
            if (success.isNotEmpty()) {
                _isLoading.value = false
                _messageSuccess.value = "Berhasil menghapus history"
                _isHistoryDeleted.value = true
            }
        }
    }

    fun findApps(appName: String = "") {
        PelindoAppsRepo.findPelindoApps(appName = "") { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@findPelindoApps
            }
            response?.let {
                appsData = it
                _appsListName.clear()
                _appsListName.addAll(getAppListNameFromFindAppsResponse(it))
            }
        }
    }

    private fun getAppListNameFromFindAppsResponse(data: PelindoAppsListResponse): List<String> {
        val appList = mutableListOf<String>()
        for (app in data.apps) {
            appList.add(app.appsName)
        }
        return appList
    }

    private fun setCountAndMinute(data: HistoryAppsListResponse) {
        val count = data.histories.count()
        var totalMinute = 0
        for (history in data.histories) {
            if (history.durationMinute > 0) {
                totalMinute += history.durationMinute
            }
        }
        val minuteToHourString = TranslateMinuteToHour(totalMinute).getStringHour()
        _count.value = "Total : $count"
        _minute.value = "Total waktu : $minuteToHourString"
    }

}