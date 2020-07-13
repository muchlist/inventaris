package com.muchlis.inventaris.views.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComputerDetailViewModel : ViewModel() {

    private val apiService: ApiService = Api.retrofitService

    //Data untuk detail
    private val _computerData: MutableLiveData<ComputerDetailResponse> = MutableLiveData()
    fun getComputerData(): MutableLiveData<ComputerDetailResponse> {
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


    fun getComputer(computerID : String) {
        _isLoading.value = true
        _messageError.value = ""
        apiService.getComputerDetail(
            token = App.prefs.authTokenSave,
            id = computerID
        ).enqueue(object : Callback<ComputerDetailResponse> {
            override fun onResponse(
                call: Call<ComputerDetailResponse>,
                response: Response<ComputerDetailResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        _computerData.postValue(response.body())
                        _isLoading.value = false
                    }
                    response.code() == 400 -> {
                        _messageError.value = "gagal memuat"
                        _isLoading.value = false
                    }
                    else -> {
                        _messageError.value = response.code().toString()
                        _isLoading.value = false
                    }
                }
            }

            override fun onFailure(call: Call<ComputerDetailResponse>, t: Throwable) {
                _isLoading.value = false
                _messageError.value = "Gagal terhubung ke server"
            }
        })
    }

}