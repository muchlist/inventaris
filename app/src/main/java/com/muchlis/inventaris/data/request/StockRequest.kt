package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class StockRequest(
    @Json(name = "category")
    val category: String,
    @Json(name = "deactive")
    val deactive: Boolean,
    @Json(name = "location")
    val location: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "qty")
    val qty: Double,
    @Json(name = "stock_name")
    val stockName: String,
    @Json(name = "threshold")
    val threshold: Double,
    @Json(name = "unit")
    val unit: String
)