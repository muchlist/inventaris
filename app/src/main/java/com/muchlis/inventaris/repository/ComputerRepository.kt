package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindComputersDto
import com.muchlis.inventaris.data.request.ComputerEditRequest
import com.muchlis.inventaris.data.request.ComputerRequest
import com.muchlis.inventaris.data.request.JustTimeStampRequest
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.data.response.ComputerListResponse
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

object ComputerRepository {
    private val apiService: ApiService = Api.retrofitService

    fun getComputer(
        computerID: String,
        callback: (response: ComputerDetailResponse?, error: String) -> Unit
    ) {
        apiService.getComputerDetail(
            token = App.prefs.authTokenSave,
            id = computerID
        ).enqueue(object : Callback<ComputerDetailResponse> {
            override fun onResponse(
                call: Call<ComputerDetailResponse>,
                response: Response<ComputerDetailResponse>
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

            override fun onFailure(call: Call<ComputerDetailResponse>, t: Throwable) {
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

    fun findComputers(
        data: FindComputersDto,
        callback: (response: ComputerListResponse?, error: String) -> Unit
    ) {
        apiService.getComputerList(
            token = App.prefs.authTokenSave,
            branch = data.branch,
            ipAddress = data.ipAddress,
            clientName = data.clientName,
            deactive = data.deactive,
            location = data.location,
            division = data.division,
            seat = data.seat
        ).enqueue(object : Callback<ComputerListResponse> {
            override fun onResponse(
                call: Call<ComputerListResponse>,
                response: Response<ComputerListResponse>
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

            override fun onFailure(call: Call<ComputerListResponse>, t: Throwable) {
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

    fun createComputer(
        args: ComputerRequest,
        callback: (response: ComputerDetailResponse?, error: String) -> Unit
    ) {
        apiService.postComputer(
            token = App.prefs.authTokenSave,
            args = args
        ).enqueue(object : Callback<ComputerDetailResponse> {
            override fun onResponse(
                call: Call<ComputerDetailResponse>,
                response: Response<ComputerDetailResponse>
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

            override fun onFailure(call: Call<ComputerDetailResponse>, t: Throwable) {
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

    fun editComputer(
        computerID: String,
        args: ComputerEditRequest,
        callback: (response: ComputerDetailResponse?, error: String) -> Unit
    ) {
        apiService.editComputerDetail(
            token = App.prefs.authTokenSave,
            id = computerID,
            args = args
        ).enqueue(object : Callback<ComputerDetailResponse> {
            override fun onResponse(
                call: Call<ComputerDetailResponse>,
                response: Response<ComputerDetailResponse>
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

            override fun onFailure(call: Call<ComputerDetailResponse>, t: Throwable) {
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


    fun deleteComputer(
        computerID: String,
        callback: (success: String, error: String) -> Unit
    ) {
        apiService.deleteComputerDetail(
            token = App.prefs.authTokenSave,
            id = computerID
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


    fun changeStatusComputer(
        computerID: String,
        statusActive: String,
        args: JustTimeStampRequest,
        callback: (response: ComputerDetailResponse?, error: String) -> Unit
    ) {
        apiService.changeStatusActive(
            token = App.prefs.authTokenSave,
            id = computerID,
            active = statusActive,
            args = args
        ).enqueue(object : Callback<ComputerDetailResponse> {
            override fun onResponse(
                call: Call<ComputerDetailResponse>,
                response: Response<ComputerDetailResponse>
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

            override fun onFailure(call: Call<ComputerDetailResponse>, t: Throwable) {
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