package com.muchlis.inventaris.repository

import com.muchlis.inventaris.data.dto.FindComputersDto
import com.muchlis.inventaris.data.response.ComputerDetailResponse
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.services.Api
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.ERR_CONN
import com.muchlis.inventaris.utils.ERR_JSON_PARSING
import com.muchlis.inventaris.utils.JsonMarshaller
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComputerRepository {
    private var apiService = Api.retrofitService

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
                    response.code() == 400 -> {
                        callback(null, "Gagal memuat data")
                    }
                    response.code() == 422 -> {
                        callback(null, "Token Expired")
                        App.prefs.authTokenSave = ""
                    }
                    else -> {
                        callback(null, response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ComputerDetailResponse>, t: Throwable) {
                callback(null, ERR_CONN)
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
            clientName = data.clientName
        ).enqueue(object : Callback<ComputerListResponse> {
            override fun onResponse(
                call: Call<ComputerListResponse>,
                response: Response<ComputerListResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        callback(response.body(), "")
                    }
                    response.code() == 400 -> {
                        callback(null, "Gagal memuat data")
                    }
                    else -> {
                        callback(null, response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ComputerListResponse>, t: Throwable) {
                callback(null, ERR_CONN)
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
                    response.code() == 400 -> {
                        val responseBody = response.errorBody()?.string() ?: ""
                        callback("", JsonMarshaller().getError(responseBody)?.message?: ERR_JSON_PARSING)
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