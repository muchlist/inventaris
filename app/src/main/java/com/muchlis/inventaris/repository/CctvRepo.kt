package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindCctvDto
import com.muchlis.inventaris.data.request.*
import com.muchlis.inventaris.data.response.CctvDetailResponse
import com.muchlis.inventaris.data.response.CctvListResponse
import com.muchlis.inventaris.data.response.ComputerDetailResponse
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

object CctvRepo {
    private val apiService: ApiService = Api.retrofitService

    fun getCctv(
        cctvID: String,
        callback: (response: CctvDetailResponse?, error: String) -> Unit
    ) {
        apiService.getCctvDetail(
            token = App.prefs.authTokenSave,
            id = cctvID
        ).enqueue(object : Callback<CctvDetailResponse> {
            override fun onResponse(
                call: Call<CctvDetailResponse>,
                response: Response<CctvDetailResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        callback(response.body(), "")
                    }
                    response.code() == 400 || response.code() == 404 || response.code() == 500 -> {
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

            override fun onFailure(call: Call<CctvDetailResponse>, t: Throwable) {
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

    fun findCctvs(
        data: FindCctvDto,
        callback: (response: CctvListResponse?, error: String) -> Unit
    ) {
        apiService.getCctvList(
            token = App.prefs.authTokenSave,
            branch = data.branch,
            ipAddress = data.ipAddress,
            cctvName = data.cctvName,
            deactive = data.deactive
        ).enqueue(object : Callback<CctvListResponse> {
            override fun onResponse(
                call: Call<CctvListResponse>,
                response: Response<CctvListResponse>
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

            override fun onFailure(call: Call<CctvListResponse>, t: Throwable) {
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

    fun createCctv(
        args: CctvRequest,
        callback: (response: CctvDetailResponse?, error: String) -> Unit
    ) {
        apiService.postCctv(
            token = App.prefs.authTokenSave,
            args = args
        ).enqueue(object : Callback<CctvDetailResponse> {
            override fun onResponse(
                call: Call<CctvDetailResponse>,
                response: Response<CctvDetailResponse>
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

            override fun onFailure(call: Call<CctvDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("to connect")){
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun editCctv(
        cctvID: String,
        args: CctvEditRequest,
        callback: (response: CctvDetailResponse?, error: String) -> Unit
    ) {
        apiService.editCctvDetail(
            token = App.prefs.authTokenSave,
            id = cctvID,
            args = args
        ).enqueue(object : Callback<CctvDetailResponse> {
            override fun onResponse(
                call: Call<CctvDetailResponse>,
                response: Response<CctvDetailResponse>
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

            override fun onFailure(call: Call<CctvDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("to connect")){
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun deleteCctv(
        cctvID: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deleteCctvDetail(
            token = App.prefs.authTokenSave,
            id = cctvID
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when {
                    response.isSuccessful -> {
                        callback("Cctv berhasil dihapus", "")
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


    fun changeStatusCctv(
        cctvID: String,
        statusActive: String,
        args: JustTimeStampRequest,
        callback: (response: CctvDetailResponse?, error: String) -> Unit
    ) {
        apiService.changeStatusActiveCctv(
            token = App.prefs.authTokenSave,
            id = cctvID,
            active = statusActive,
            args = args
        ).enqueue(object : Callback<CctvDetailResponse> {
            override fun onResponse(
                call: Call<CctvDetailResponse>,
                response: Response<CctvDetailResponse>
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

            override fun onFailure(call: Call<CctvDetailResponse>, t: Throwable) {
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