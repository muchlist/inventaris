package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {

    private val apiService: ApiService = Api.retrofitService

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

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess

    init {
        _isLoading.value = false
        _messageError.value = ""
    }


    fun findHistories(branch: String, category: String, limit: Int) {
        _isLoading.value = true
        _messageError.value = ""
        apiService.getHistory(
            token = App.prefs.authTokenSave,
            branch = branch,
            category = category,
            limit = limit
        ).enqueue(object : Callback<HistoryListResponse> {
            override fun onResponse(
                call: Call<HistoryListResponse>,
                response: Response<HistoryListResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        val result = response.body()
                        _historyData.postValue(result)
                        _isLoading.value = false
                    }
                    response.code() == 400 -> {
                        _messageError.value = "gagal memuat"
                        _isLoading.value = false
                    }
                    response.code() == 422 -> {
                        _messageError.value = "Token Expired"
                        _isLoading.value = false
                        App.prefs.authTokenSave = ""
                    }
                    else -> {
                        _messageError.value = response.code().toString()
                        _isLoading.value = false
                    }
                }
            }

            override fun onFailure(call: Call<HistoryListResponse>, t: Throwable) {
                _isLoading.value = false
                _messageError.value = t.message//"Gagal terhubung ke server"
            }
        })
    }


    fun getOptions(){
        apiService.getOptions(App.prefs.authTokenSave).enqueue(object : Callback<ResponseBody>{

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when {
                    response.isSuccessful -> {
                        App.prefs.optionsJson = response.body()?.string() ?: ""
                        _isLoading.value = false
                    }
                    else -> {
                        _messageError.value = response.code().toString()
                        _isLoading.value = false
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _messageError.value = t.message
            }

        })
    }

}