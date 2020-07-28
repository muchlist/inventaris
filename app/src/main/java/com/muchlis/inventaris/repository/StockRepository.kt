package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindStocksDto
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.data.response.StockListResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object StockRepository {
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
                callback(null, ERR_CONN)
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
            deactive = data.deactive
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
                callback(null, ERR_CONN)
            }
        })
    }


    private fun getMsgFromJson(errorBody: String): String {
        val jsonMarshaller = JsonMarshaller()
        return jsonMarshaller.getError(errorBody)?.msg ?: ERR_JSON_PARSING
    }
}