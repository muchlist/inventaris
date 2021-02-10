package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindHistoryDto
import com.muchlis.inventaris.data.request.HistoryEditRequest
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.data.response.DashboardResponse
import com.muchlis.inventaris.data.response.ErrorResponse
import com.muchlis.inventaris.data.response.HistoryListResponse
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object HistoryRepo {
    private val apiService = Api.retrofitService

    fun getHistories(
        data: FindHistoryDto,
        callback: (response: HistoryListResponse?, error: String) -> Unit
    ) {
        apiService.getHistory(
            token = App.prefs.authTokenSave,
            branch = data.branch,
            category = data.category,
            limit = data.limit,
//            isComplete = data.isComplete
            completeStatus = data.completeStatus
        ).enqueue(object : Callback<HistoryListResponse> {
            override fun onResponse(
                call: Call<HistoryListResponse>,
                response: Response<HistoryListResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        val result = response.body()
                        callback(result, "")
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

            override fun onFailure(call: Call<HistoryListResponse>, t: Throwable) {
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


    fun getDashboard(
        callback: (response: DashboardResponse?, error: String) -> Unit
    ) {
        apiService.getDashboard(
            token = App.prefs.authTokenSave,
        ).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(
                call: Call<DashboardResponse>,
                response: Response<DashboardResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        val result = response.body()
                        callback(result, "")
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

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
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


    fun createHistory(
        parentID: String,
        args: HistoryRequest,
        callback: (response: ErrorResponse?, error: String) -> Unit
    ) {
        apiService.postHistory(
            token = App.prefs.authTokenSave,
            id = parentID,
            args = args
        ).enqueue(object : Callback<ErrorResponse> {
            override fun onResponse(
                call: Call<ErrorResponse>,
                response: Response<ErrorResponse>
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

            override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
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

    fun findHistoriesForParent(
        parentID: String,
        callback: (response: HistoryListResponse?, error: String) -> Unit
    ) {
        apiService.getHistoryFromParent(
            token = App.prefs.authTokenSave,
            id = parentID
        ).enqueue(object : Callback<HistoryListResponse> {
            override fun onResponse(
                call: Call<HistoryListResponse>,
                response: Response<HistoryListResponse>
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
                        callback(response.body(), response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<HistoryListResponse>, t: Throwable) {
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


    fun deleteHistory(
        historyID: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deleteSimpleHistory(
            token = App.prefs.authTokenSave,
            id = historyID
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when {
                    response.isSuccessful -> {
                        callback("Berhasil menghapus history", "")
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

    fun getDetailHistory(
        id: String,
        callback: (response: HistoryResponse?, error: String) -> Unit
    ) {
        apiService.getHistoryDetail(
            token = App.prefs.authTokenSave,
            id = id
        ).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        val result = response.body()
                        callback(result, "")
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

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
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


    fun editHistory(
        historyID: String,
        args: HistoryEditRequest,
        callback: (response: HistoryResponse?, error: String) -> Unit
    ) {
        apiService.editHistoryDetail(
            token = App.prefs.authTokenSave,
            id = historyID,
            args = args
        ).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
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

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
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