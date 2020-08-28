package com.muchlis.inventaris.data.response


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
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
    val decrement: List<IncrementDecrement>,
    @Json(name = "_id")
    val id: String,
    @Json(name = "increment")
    val increment: List<IncrementDecrement>,
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
    @Json(name = "image")
    val image: String,
    @Json(name = "updated_at")
    val updatedAt: String
) : Parcelable {
    @Parcelize
    data class IncrementDecrement(
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
    ) : Parcelable
}