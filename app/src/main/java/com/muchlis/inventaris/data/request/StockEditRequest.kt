package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class StockEditRequest(
    @Json(name = "category")
    val category: String,
    @Json(name = "deactive")
    val deactive: Boolean,
    @Json(name = "location")
    val location: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "stock_name")
    val stockName: String,
    @Json(name = "threshold")
    val threshold: Double,
    @Json(name = "timestamp")
    val timestamp: String,
    @Json(name = "unit")
    val unit: String
)