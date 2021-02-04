package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class CheckRequest(
    @Json(name = "shift")
    val shift: Int
)