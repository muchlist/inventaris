package com.muchlis.inventaris.repository

import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object OptionSelectorRepo {
    private val apiService = Api.retrofitService

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
                t.message?.let {
                    if (it.contains("Failed to connect")){
                        callback("", ERR_CONN)
                    } else {
                        callback("", it)
                    }
                }
            }
        })
    }
}