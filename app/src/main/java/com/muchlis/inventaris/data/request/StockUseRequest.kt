package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class StockUseRequest(
    @Json(name = "ba_number")
    val baNumber: String,
    @Json(name = "mode")
    val mode: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "qty")
    val qty: Double,
    @Json(name = "time")
    val time: String
) {
    fun isValid(): Boolean {
        if (mode.isEmpty() ||
            note.isEmpty() ||
            qty == 0.0
        ) {
            return false
        }
        return true
    }
}