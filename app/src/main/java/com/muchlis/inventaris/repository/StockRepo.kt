package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindStocksDto
import com.muchlis.inventaris.data.request.JustTimeStampRequest
import com.muchlis.inventaris.data.request.StockEditRequest
import com.muchlis.inventaris.data.request.StockRequest
import com.muchlis.inventaris.data.request.StockUseRequest
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.data.response.StockListResponse
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

object StockRepo {
    private val apiService: ApiService = Api.retrofitService

    fun getStock(
        computerID: String,
        callback: (response: StockDetailResponse?, error: String) -> Unit
    ) {
        apiService.getStockDetail(
            token = App.prefs.authTokenSave,
            id = computerID
        ).enqueue(object : Callback<StockDetailResponse> {
            override fun onResponse(
                call: Call<StockDetailResponse>,
                response: Response<StockDetailResponse>
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

            override fun onFailure(call: Call<StockDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("Failed to connect")){
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun findStocks(
        data: FindStocksDto,
        callback: (response: StockListResponse?, error: String) -> Unit
    ) {
        apiService.getStockList(
            token = App.prefs.authTokenSave,
            branch = data.branch,
            stockName = data.stockName,
            deactive = data.deactive,
            category = data.category,
            location = data.location
        ).enqueue(object : Callback<StockListResponse> {
            override fun onResponse(
                call: Call<StockListResponse>,
                response: Response<StockListResponse>
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

            override fun onFailure(call: Call<StockListResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("Failed to connect")){
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun createStock(
        args: StockRequest,
        callback: (response: StockDetailResponse?, error: String) -> Unit
    ) {
        apiService.postStock(
            token = App.prefs.authTokenSave,
            args = args
        ).enqueue(object : Callback<StockDetailResponse> {
            override fun onResponse(
                call: Call<StockDetailResponse>,
                response: Response<StockDetailResponse>
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

            override fun onFailure(call: Call<StockDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("Failed to connect")){
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }


    fun editStock(
        stockID: String,
        args: StockEditRequest,
        callback: (response: StockDetailResponse?, error: String) -> Unit
    ) {
        apiService.editStockDetail(
            token = App.prefs.authTokenSave,
            id = stockID,
            args = args
        ).enqueue(object : Callback<StockDetailResponse> {
            override fun onResponse(
                call: Call<StockDetailResponse>,
                response: Response<StockDetailResponse>
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

            override fun onFailure(call: Call<StockDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("Failed to connect")){
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun deleteStock(
        stockID: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deleteStockDetail(
            token = App.prefs.authTokenSave,
            id = stockID
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                when {
                    response.isSuccessful -> {
                        callback("Komputer berhasil dihapus", "")
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
                    if (it.contains("Failed to connect")){
                        callback("", ERR_CONN)
                    } else {
                        callback("", it)
                    }
                }
            }
        })
    }

    fun changeStatusStock(
        stockID: String,
        statusActive: String,
        args: JustTimeStampRequest,
        callback: (response: StockDetailResponse?, error: String) -> Unit
    ) {
        apiService.changeStatusActiveStock(
            token = App.prefs.authTokenSave,
            id = stockID,
            active = statusActive,
            args = args
        ).enqueue(object : Callback<StockDetailResponse> {
            override fun onResponse(
                call: Call<StockDetailResponse>,
                response: Response<StockDetailResponse>
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

            override fun onFailure(call: Call<StockDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("Failed to connect")){
                        callback(null, ERR_CONN)
                    } else {
                        callback(null, it)
                    }
                }
            }
        })
    }

    fun useStock(
        parentID: String,
        args: StockUseRequest,
        callback: (response: StockDetailResponse?, error: String) -> Unit
    ) {
        apiService.useStock(
            token = App.prefs.authTokenSave,
            id = parentID,
            args = args
        ).enqueue(object : Callback<StockDetailResponse> {
            override fun onResponse(
                call: Call<StockDetailResponse>,
                response: Response<StockDetailResponse>
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

            override fun onFailure(call: Call<StockDetailResponse>, t: Throwable) {
                t.message?.let {
                    if (it.contains("Failed to connect")){
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