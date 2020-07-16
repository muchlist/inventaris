package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class HistoryRequest(
    @Json(name = "category")
    val category: String,
    @Json(name = "date")
    val date: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "status")
    val status: String
) {
    fun isValid(): Boolean {
        if (category.isEmpty() || status.isEmpty() || date.isEmpty()) {
            return false
        }
        return true
    }
}