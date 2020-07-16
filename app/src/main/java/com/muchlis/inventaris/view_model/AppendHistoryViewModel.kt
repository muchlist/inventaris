package com.muchlis.inventaris.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppendHistoryViewModel : ViewModel() {
    private val apiService: ApiService = Api.retrofitService

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

    fun createHistory(parentID : String, args: HistoryRequest) {
        _isLoading.value = true
        _isHistoryCreated.value = false
        apiService.postHistory(
            token = App.prefs.authTokenSave,
            id = parentID,
            args = args
        ).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        _isLoading.value = false
                        _messageSuccess.value = "Menambahkan riwayat berhasil"
                        _isHistoryCreated.value = true
                    }
                    response.code() == 400 -> {
                        val responseBody = response.errorBody()?.string() ?: ""
                        _messageError.value =
                            JsonMarshaller().getError(responseBody)?.message ?: ERR_JSON_PARSING
                        _isLoading.value = false
                    }
                    else -> {
                        _messageError.value = response.code().toString()
                        _isLoading.value = false
                    }
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                _isLoading.value = false
                _messageError.value = ERR_CONN
            }
        })
    }
}