package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindAppHistoryDto
import com.muchlis.inventaris.data.dto.FindHistoryDto
import com.muchlis.inventaris.data.request.ComputerEditRequest
import com.muchlis.inventaris.data.request.HistoryAppsEditRequest
import com.muchlis.inventaris.data.request.HistoryAppsRequest
import com.muchlis.inventaris.data.request.HistoryRequest
import com.muchlis.inventaris.data.response.*
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PelindoAppsRepo {

    private val apiService = Api.retrofitService

    fun findPelindoApps(
        appName: String,
        callback: (response: PelindoAppsListResponse?, error: String) -> Unit
    ) {
        apiService.getPelindoAppsList(
            token = App.prefs.authTokenSave,
            appsName = appName
        ).enqueue(object : Callback<PelindoAppsListResponse> {
            override fun onResponse(
                call: Call<PelindoAppsListResponse>,
                response: Response<PelindoAppsListResponse>
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

            override fun onFailure(call: Call<PelindoAppsListResponse>, t: Throwable) {
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


    fun getDetailPelindoApps(
        id: String,
        callback: (response: PelindoAppsDetailResponse?, error: String) -> Unit
    ) {
        apiService.getAppsDetail(
            token = App.prefs.authTokenSave,
            id = id
        ).enqueue(object : Callback<PelindoAppsDetailResponse> {
            override fun onResponse(
                call: Call<PelindoAppsDetailResponse>,
                response: Response<PelindoAppsDetailResponse>
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

            override fun onFailure(call: Call<PelindoAppsDetailResponse>, t: Throwable) {
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



    fun createPelindoAppsHistory(
        parentID: String,
        args: HistoryAppsRequest,
        callback: (response: ErrorResponse?, error: String) -> Unit
    ) {
        apiService.createAppsHistoryForParent(
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
                    if (it.contains("to connect")){
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun findPelindoAppsHistory(
        dto: FindAppHistoryDto,
        callback: (response: HistoryAppsListResponse?, error: String) -> Unit
    ) {
        apiService.getPelindoAppsHistoryList(
            token = App.prefs.authTokenSave,
            appName = dto.appName,
            branch = dto.branch,
            category = dto.category,
            limit = dto.limit
        ).enqueue(object : Callback<HistoryAppsListResponse> {
            override fun onResponse(
                call: Call<HistoryAppsListResponse>,
                response: Response<HistoryAppsListResponse>
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

            override fun onFailure(call: Call<HistoryAppsListResponse>, t: Throwable) {
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

    fun findPelindoAppsHistoryForParent(
        parentID: String,
        callback: (response: HistoryAppsListResponse?, error: String) -> Unit
    ) {
        apiService.getPelindoAppsHistoryListByParent(
            token = App.prefs.authTokenSave,
            id = parentID
        ).enqueue(object : Callback<HistoryAppsListResponse> {
            override fun onResponse(
                call: Call<HistoryAppsListResponse>,
                response: Response<HistoryAppsListResponse>
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

            override fun onFailure(call: Call<HistoryAppsListResponse>, t: Throwable) {
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


    fun getDetailPelindoAppsHistory(
        id: String,
        callback: (response: HistoryAppsDetailResponse?, error: String) -> Unit
    ) {
        apiService.getAppsHistoryDetail(
            token = App.prefs.authTokenSave,
            id = id
        ).enqueue(object : Callback<HistoryAppsDetailResponse> {
            override fun onResponse(
                call: Call<HistoryAppsDetailResponse>,
                response: Response<HistoryAppsDetailResponse>
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

            override fun onFailure(call: Call<HistoryAppsDetailResponse>, t: Throwable) {
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


    fun editPelindoAppsHistory(
        historyID: String,
        args: HistoryAppsEditRequest,
        callback: (response: HistoryAppsDetailResponse?, error: String) -> Unit
    ) {
        apiService.editAppsHistoryDetail(
            token = App.prefs.authTokenSave,
            id = historyID,
            args = args
        ).enqueue(object : Callback<HistoryAppsDetailResponse> {
            override fun onResponse(
                call: Call<HistoryAppsDetailResponse>,
                response: Response<HistoryAppsDetailResponse>
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

            override fun onFailure(call: Call<HistoryAppsDetailResponse>, t: Throwable) {
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


    fun deletePelindoAppsHistory(
        historyID: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deletePelindoAppsHistory(
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
                    if (it.contains("to connect")){
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