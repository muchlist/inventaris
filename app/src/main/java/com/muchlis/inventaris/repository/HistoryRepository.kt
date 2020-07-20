package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindHistoryDto
import com.muchlis.inventaris.data.request.HistoryRequest
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

class HistoryRepository {
    private var apiService = Api.retrofitService

    fun getHistories(
        data: FindHistoryDto,
        callback: (response: HistoryListResponse?, error: String) -> Unit
    ) {
        apiService.getHistory(
            token = App.prefs.authTokenSave,
            branch = data.branch,
            category = data.category,
            limit = data.limit
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
                    response.code() == 400 -> {
                        callback(null, "Gagal memuat")
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
                callback(null, ERR_CONN)
            }
        })
    }

    fun createHistory(
        parentID: String,
        args: HistoryRequest,
        callback: (response: HistoryResponse?, error: String) -> Unit
    ) {
        apiService.postHistory(
            token = App.prefs.authTokenSave,
            id = parentID,
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
                    response.code() == 400 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        callback(
                            null,
                            JsonMarshaller().getError(errorBody)?.message ?: ERR_JSON_PARSING
                        )
                    }
                    else -> {
                        callback(null, response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                callback(null, ERR_CONN)
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
                    response.code() == 400 -> {
                        callback(response.body(), "Gagal memuat")
                    }
                    else -> {
                        callback(response.body(), response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<HistoryListResponse>, t: Throwable) {
                callback(null, ERR_CONN)
            }
        })
    }


    fun deleteHistory(
        historyID: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deleteComputerHistory(
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
                    response.code() == 400 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        callback(
                            "",
                            JsonMarshaller().getError(errorBody)?.message ?: ERR_JSON_PARSING
                        )
                    }
                    else -> {
                        callback("", response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback("", ERR_CONN)
            }
        })
    }

}