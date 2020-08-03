package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindCctvDto
import com.muchlis.inventaris.data.dto.FindComputersDto
import com.muchlis.inventaris.data.response.CctvListResponse
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.services.ApiService
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CctvRepository {
    private val apiService: ApiService = Api.retrofitService

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
                callback(null, ERR_CONN)
            }
        })
    }

    private fun getMsgFromJson(errorBody: String): String {
        val jsonMarshaller = JsonMarshaller()
        return jsonMarshaller.getError(errorBody)?.msg ?: ERR_JSON_PARSING
    }
}