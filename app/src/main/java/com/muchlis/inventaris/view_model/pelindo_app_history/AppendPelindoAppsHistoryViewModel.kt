package com.muchlis.inventaris.view_model.pelindo_app_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.HistoryAppsRequest
import com.muchlis.inventaris.data.response.PelindoAppsListResponse
import com.muchlis.inventaris.repository.PelindoAppsRepo

class AppendPelindoAppsHistoryViewModel : ViewModel() {

    private lateinit var  appsData: PelindoAppsListResponse

    private val _appsListName: MutableLiveData<List<String>> = MutableLiveData()
    fun getAppsListName(): MutableLiveData<List<String>> {
        return _appsListName
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isHistoryCreated = MutableLiveData<Boolean>()
    val isHistoryCreated: LiveData<Boolean>
        get() = _isHistoryCreated

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess


    init {
        _isLoading.value = false
        _isHistoryCreated.value = false
        _messageError.value = ""
        _messageSuccess.value = ""
    }

    fun appendHistory(parentID: String, args: HistoryAppsRequest) {
        _isLoading.value = true
        _isHistoryCreated.value = false
        PelindoAppsRepo.createPelindoAppsHistory(
            parentID = parentID,
            args = args
        ) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@createPelindoAppsHistory
            }
            response.let {
                _isLoading.value = false
                _messageSuccess.value = "Menambahkan riwayat berhasil"
                _isHistoryCreated.value = true
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
                _appsListName.postValue(getAppListNameFromFindAppsResponse(it))
            }
        }
    }

    private fun getAppListNameFromFindAppsResponse(data: PelindoAppsListResponse): List<String>{
        val appList = mutableListOf<String>()
        for (app in data.apps){
            appList.add(app.appsName)
        }
        return appList
    }

    fun getIDFromAppName(appName: String): String?{
        if (appsData.apps.count() != 0){
            for (data in appsData.apps){
                if (data.appsName == appName){
                    return data.id
                }
            }
        }
        return null
    }
}