package com.muchlis.inventaris.data.response


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class ErrorResponse(
    @SerializedName("message")
    @Json(name = "message")
    val message: String
)