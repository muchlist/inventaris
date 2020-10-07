package com.muchlis.inventaris.repository

import android.util.Log
import com.muchlis.inventaris.data.dto.FindHandheldDto
import com.muchlis.inventaris.data.request.HandheldEditRequest
import com.muchlis.inventaris.data.request.HandheldRequest
import com.muchlis.inventaris.data.request.JustTimeStampRequest
import com.muchlis.inventaris.data.response.HandheldDetailResponse
import com.muchlis.inventaris.data.response.HandheldListResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object HandheldRepo {
    private val apiService: ApiService = Api.retrofitService

    fun getHandheld(
        handheldID: String,
        callback: (response: HandheldDetailResponse?, error: String) -> Unit
    ) {
        apiService.getHandheldDetail(
            token = App.prefs.authTokenSave,
            id = handheldID
        ).enqueue(object : Callback<HandheldDetailResponse> {
            override fun onResponse(
                call: Call<HandheldDetailResponse>,
                response: Response<HandheldDetailResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        callback(response.body(), "")
                    }
                    response.code() == 400 || response.code() == 500 -> {
                        val responseBody = response.errorBody()?.string() ?: ""
                        callback(
                            null,
                            getMsgFromJson(responseBody)
                        )
                    }
                    else -> {
                        callback(null, response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<HandheldDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("to connect")) {
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun findHandhelds(
        data: FindHandheldDto,
        callback: (response: HandheldListResponse?, error: String) -> Unit
    ) {
        apiService.getHandheldList(
            token = App.prefs.authTokenSave,
            branch = data.branch,
            handheldName = data.handheldName,
            deactive = data.deactive,
            location = data.location,
        ).enqueue(object : Callback<HandheldListResponse> {
            override fun onResponse(
                call: Call<HandheldListResponse>,
                response: Response<HandheldListResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        callback(response.body(), "")
                    }
                    response.code() == 400 || response.code() == 500 -> {
                        val responseBody = response.errorBody()?.string() ?: ""
                        callback(
                            null,
                            getMsgFromJson(responseBody)
                        )
                    }
                    response.code() == 422 || response.code() == 401 -> {
                        callback(null, "Token Expired")
                        App.prefs.authTokenSave = ""
                    }
                    else -> {
                        callback(null, response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<HandheldListResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("to connect")) {
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun createHandheld(
        args: HandheldRequest,
        callback: (response: HandheldDetailResponse?, error: String) -> Unit
    ) {
        apiService.postHandheld(
            token = App.prefs.authTokenSave,
            args = args
        ).enqueue(object : Callback<HandheldDetailResponse> {
            override fun onResponse(
                call: Call<HandheldDetailResponse>,
                response: Response<HandheldDetailResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        callback(response.body(), "")
                    }
                    response.code() == 400 || response.code() == 500 -> {
                        val responseBody = response.errorBody()?.string() ?: ""
                        callback(
                            null,
                            getMsgFromJson(responseBody)
                        )
                    }
                    else -> {
                        callback(null, response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<HandheldDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("to connect")) {
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun editHandheld(
        handheldID: String,
        args: HandheldEditRequest,
        callback: (response: HandheldDetailResponse?, error: String) -> Unit
    ) {
        apiService.editHandheldDetail(
            token = App.prefs.authTokenSave,
            id = handheldID,
            args = args
        ).enqueue(object : Callback<HandheldDetailResponse> {
            override fun onResponse(
                call: Call<HandheldDetailResponse>,
                response: Response<HandheldDetailResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        callback(response.body(), "")
                    }
                    response.code() == 400 || response.code() == 500 -> {
                        val responseBody = response.errorBody()?.string() ?: ""
                        callback(
                            null,
                            getMsgFromJson(responseBody)
                        )
                    }
                    else -> {
                        callback(null, response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<HandheldDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("to connect")) {
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }


    fun deleteHandheld(
        handheldID: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deleteHandheldDetail(
            token = App.prefs.authTokenSave,
            id = handheldID
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when {
                    response.isSuccessful -> {
                        callback("Handheld berhasil dihapus", "")
                    }
                    response.code() == 400 || response.code() == 500 -> {
                        val responseBody = response.errorBody()?.string() ?: ""
                        callback(
                            "",
                            getMsgFromJson(responseBody)
                        )
                    }
                    else -> {
                        callback("", response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let {
                    if (it.contains("to connect")) {
                        callback("", ERR_CONN)
                    } else {
                        callback("", it)
                    }
                }
            }
        })
    }


    fun changeStatusHandheld(
        handheldID: String,
        statusActive: String,
        args: JustTimeStampRequest,
        callback: (response: HandheldDetailResponse?, error: String) -> Unit
    ) {
        apiService.changeStatusHHActive(
            token = App.prefs.authTokenSave,
            id = handheldID,
            active = statusActive,
            args = args
        ).enqueue(object : Callback<HandheldDetailResponse> {
            override fun onResponse(
                call: Call<HandheldDetailResponse>,
                response: Response<HandheldDetailResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        callback(response.body(), "")
                    }
                    response.code() == 400 || response.code() == 500 -> {
                        val responseBody = response.errorBody()?.string() ?: ""
                        callback(
                            null,
                            getMsgFromJson(responseBody)
                        )
                    }
                    else -> {
                        callback(null, response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<HandheldDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("to connect")) {
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }


    private fun getMsgFromJson(errorBody: String): String {
        val jsonMarshaller = JsonMarshaller()
        return jsonMarshaller.getError(errorBody)?.msg ?: ERR_JSON_PARSING
    }

}