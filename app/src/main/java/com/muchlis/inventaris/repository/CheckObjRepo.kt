package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindStocksDto
import com.muchlis.inventaris.data.request.*
import com.muchlis.inventaris.data.response.CheckObjResponse
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.data.response.StockListResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CheckObjRepo {
    private val apiService: ApiService = Api.retrofitService

    fun getCheckObj(
        id: String,
        callback: (response: CheckObjResponse.CheckObj?, error: String) -> Unit
    ) {
        apiService.getCheckObj(
            token = App.prefs.authTokenSave,
            id = id
        ).enqueue(object : Callback<CheckObjResponse.CheckObj> {
            override fun onResponse(
                call: Call<CheckObjResponse.CheckObj>,
                response: Response<CheckObjResponse.CheckObj>
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

            override fun onFailure(call: Call<CheckObjResponse.CheckObj>, t: Throwable) {
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

    fun findCheckObj(
        name: String,
        problem: Int,
        callback: (response: CheckObjResponse?, error: String) -> Unit
    ) {
        apiService.findCheckObj(
            token = App.prefs.authTokenSave,
            name = name,
            problem = problem,
        ).enqueue(object : Callback<CheckObjResponse> {
            override fun onResponse(
                call: Call<CheckObjResponse>,
                response: Response<CheckObjResponse>
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

            override fun onFailure(call: Call<CheckObjResponse>, t: Throwable) {
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

    fun createCheckObj(
        args: CheckObjRequest,
        callback: (response: CheckObjResponse.CheckObj?, error: String) -> Unit
    ) {
        apiService.postCheckObj(
            token = App.prefs.authTokenSave,
            args = args
        ).enqueue(object : Callback<CheckObjResponse.CheckObj> {
            override fun onResponse(
                call: Call<CheckObjResponse.CheckObj>,
                response: Response<CheckObjResponse.CheckObj>
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

            override fun onFailure(call: Call<CheckObjResponse.CheckObj>, t: Throwable) {
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


    fun editCheckObj(
        id: String,
        args: CheckObjEditRequest,
        callback: (response: CheckObjResponse.CheckObj?, error: String) -> Unit
    ) {
        apiService.editCheckObj(
            token = App.prefs.authTokenSave,
            id = id,
            args = args
        ).enqueue(object : Callback<CheckObjResponse.CheckObj> {
            override fun onResponse(
                call: Call<CheckObjResponse.CheckObj>,
                response: Response<CheckObjResponse.CheckObj>
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

            override fun onFailure(call: Call<CheckObjResponse.CheckObj>, t: Throwable) {
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

    fun deleteCheckObj(
        id: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deleteCheckObj(
            token = App.prefs.authTokenSave,
            id = id
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when {
                    response.isSuccessful -> {
                        callback("CheckObject berhasil dihapus", "")
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


    private fun getMsgFromJson(errorBody: String): String {
        val jsonMarshaller = JsonMarshaller()
        return jsonMarshaller.getError(errorBody)?.msg ?: ERR_JSON_PARSING
    }

}