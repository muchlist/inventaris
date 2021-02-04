package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class CheckEditRequest(
    @Json(name = "is_finish")
    val isFinish: Boolean,
    @Json(name = "shift")
    val shift: Int
)