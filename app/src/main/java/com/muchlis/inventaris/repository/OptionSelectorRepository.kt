package com.muchlis.inventaris.repository

import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.utils.App
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OptionSelectorRepository {
    private var apiService = Api.retrofitService

    fun getOptions(callback: (response: String, error: String) -> Unit) {
        apiService.getOptions(App.prefs.authTokenSave).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when {
                    response.isSuccessful -> {
                        callback(response.body()?.string() ?: "", "")
                    }
                    else -> {
                        callback("", response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback("", "Error : ${t.message}")
            }
        })
    }
}