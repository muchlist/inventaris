package com.muchlis.inventaris.data.request

import com.squareup.moshi.Json

data class JustTimeStampRequest(
    @Json(name = "timestamp")
    val timeStamp: String
)