package com.muchlis.inventaris.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.request.LoginRequest
import com.muchlis.inventaris.data.response.LoginResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val apiService: ApiService = Api.retrofitService

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
    get() = _isLoading

    private val _isLoginSuccess = MutableLiveData<Boolean>()
    val isLoginSuccess: LiveData<Boolean>
    get() = _isLoginSuccess

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String>
    get() = _isError

    init {
        _isLoading.value = false
        _isLoginSuccess.value = false
        _isError.value = ""
    }


    fun doLogin(loginInput: LoginRequest) {
        _isLoading.value = true
        _isError.value = ""
        apiService.postLogin(
            args = loginInput
        ).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        val result = response.body()!!
                        saveDataLogin(result)
                        _isLoginSuccess.value = true
                    }
                    response.code() == 400 -> {
                        _isError.value = "Username atau password salah!"
                        _isLoading.value = false
                    }
                    else -> {
                        _isError.value = response.code().toString()
                        _isLoading.value = false
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoginSuccess.value = false
                _isLoading.value = false
                _isError.value = "Gagal terhubung ke server"
            }
        })
    }

    private fun saveDataLogin(data: LoginResponse) {
        val pref = App.prefs
        pref.authTokenSave = "Bearer "+ data.accessToken
        pref.nameSave = data.name
        pref.userBranchSave = data.branch
        pref.isAdmin = data.isAdmin
        pref.isEndUser = data.isEndUser
    }

}