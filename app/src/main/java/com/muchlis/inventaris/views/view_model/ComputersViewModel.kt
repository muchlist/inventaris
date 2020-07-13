package com.muchlis.inventaris.views.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComputersViewModel : ViewModel() {

    private val apiService: ApiService = Api.retrofitService

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


    fun findComputers(branch: String, ipAddress: String, clientName: String) {
        _isLoading.value = true
        _messageError.value = ""
        apiService.getComputerList(
            token = App.prefs.authTokenSave,
            branch = branch, ipAddress = ipAddress, clientName = clientName
        ).enqueue(object : Callback<ComputerListResponse> {
            override fun onResponse(
                call: Call<ComputerListResponse>,
                response: Response<ComputerListResponse>
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

            override fun onFailure(call: Call<ComputerListResponse>, t: Throwable) {
                _isLoading.value = false
                _messageError.value = t.message//"Gagal terhubung ke server"
            }
        })
    }

}