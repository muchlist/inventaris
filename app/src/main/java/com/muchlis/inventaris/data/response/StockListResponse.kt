package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class StockListResponse(
    @Json(name = "stocks")
    val stocks: List<Stock>
) {
    data class Stock(
        @Json(name = "branch")
        val branch: String,
        @Json(name = "category")
        val category: String,
        @Json(name = "_id")
        val id: String,
        @Json(name = "location")
        val location: String,
        @Json(name = "qty")
        val qty: Double,
        @Json(name = "stock_name")
        val stockName: String,
        @Json(name = "threshold")
        val threshold: Double,
        @Json(name = "unit")
        val unit: String
    )
}