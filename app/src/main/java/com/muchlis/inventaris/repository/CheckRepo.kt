package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.request.CheckEditRequest
import com.muchlis.inventaris.data.request.CheckRequest
import com.muchlis.inventaris.data.request.CheckUpdateRequest
import com.muchlis.inventaris.data.response.CheckListResponse
import com.muchlis.inventaris.data.response.CheckResponse
import com.muchlis.inventaris.data.response.StockDetailResponse
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

object CheckRepo {
    private val apiService: ApiService = Api.retrofitService

    fun getCheck(
        id: String,
        callback: (response: CheckResponse?, error: String) -> Unit
    ) {
        apiService.getCheck(
            token = App.prefs.authTokenSave,
            id = id
        ).enqueue(object : Callback<CheckResponse> {
            override fun onResponse(
                call: Call<CheckResponse>,
                response: Response<CheckResponse>
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

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
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

    fun findCheck(
        callback: (response: CheckListResponse?, error: String) -> Unit
    ) {
        apiService.findCheck(
            token = App.prefs.authTokenSave,
        ).enqueue(object : Callback<CheckListResponse> {
            override fun onResponse(
                call: Call<CheckListResponse>,
                response: Response<CheckListResponse>
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

            override fun onFailure(call: Call<CheckListResponse>, t: Throwable) {
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

    fun createCheck(
        args: CheckRequest,
        callback: (response: CheckResponse?, error: String) -> Unit
    ) {
        apiService.postCheck(
            token = App.prefs.authTokenSave,
            args = args
        ).enqueue(object : Callback<CheckResponse> {
            override fun onResponse(
                call: Call<CheckResponse>,
                response: Response<CheckResponse>
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

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
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


    fun editCheck(
        id: String,
        args: CheckEditRequest,
        callback: (response: CheckResponse?, error: String) -> Unit
    ) {
        apiService.editCheck(
            token = App.prefs.authTokenSave,
            id = id,
            args = args
        ).enqueue(object : Callback<CheckResponse> {
            override fun onResponse(
                call: Call<CheckResponse>,
                response: Response<CheckResponse>
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

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
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


    fun updateCheck(
        parentId: String,
        childId: String,
        args: CheckUpdateRequest,
        callback: (response: CheckResponse?, error: String) -> Unit
    ) {
        apiService.updateCheckChild(
            token = App.prefs.authTokenSave,
            parentID = parentId,
            childID = childId,
            args = args
        ).enqueue(object : Callback<CheckResponse> {
            override fun onResponse(
                call: Call<CheckResponse>,
                response: Response<CheckResponse>
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

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
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


    fun uploadImageCheck(
        parentID: String,
        childId: String,
        imageFile: RequestBody,
        callback: (response: CheckResponse?, error: String) -> Unit
    ) {
        apiService.uploadImageCheck(
            token = App.prefs.authTokenSave,
            parentID = parentID,
            childID = childId,
            image = imageFile
        ).enqueue(object : Callback<CheckResponse> {
            override fun onResponse(
                call: Call<CheckResponse>,
                response: Response<CheckResponse>
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

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
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

    fun deleteCheck(
        id: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deleteCheck(
            token = App.prefs.authTokenSave,
            id = id
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when {
                    response.isSuccessful -> {
                        callback("Check berhasil dihapus", "")
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