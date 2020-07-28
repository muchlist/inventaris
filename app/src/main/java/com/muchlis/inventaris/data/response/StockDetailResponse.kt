package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class StockDetailResponse(
    @Json(name = "author")
    val author: String,
    @Json(name = "branch")
    val branch: String,
    @Json(name = "category")
    val category: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "deactive")
    val deactive: Boolean,
    @Json(name = "decrement")
    val decrement: List<Decrement>,
    @Json(name = "_id")
    val id: String,
    @Json(name = "increment")
    val increment: List<Increment>,
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
    val unit: String,
    @Json(name = "updated_at")
    val updatedAt: String
) {
    data class Decrement(
        @Json(name = "author")
        val author: String,
        @Json(name = "ba_number")
        val baNumber: String,
        @Json(name = "dummy_id")
        val dummyId: Int,
        @Json(name = "note")
        val note: String,
        @Json(name = "qty")
        val qty: Double,
        @Json(name = "time")
        val time: String
    )

    data class Increment(
        @Json(name = "author")
        val author: String,
        @Json(name = "ba_number")
        val baNumber: String,
        @Json(name = "dummy_id")
        val dummyId: Int,
        @Json(name = "note")
        val note: String,
        @Json(name = "qty")
        val qty: Double,
        @Json(name = "time")
        val time: String
    )
}