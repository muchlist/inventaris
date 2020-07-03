package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class HistoryListResponse(
    @Json(name = "histories")
    val histories: List<HistoryResponse>
)