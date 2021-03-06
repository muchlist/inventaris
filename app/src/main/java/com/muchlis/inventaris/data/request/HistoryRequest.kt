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
    val status: String,
    @Json(name = "end_date")
    val endDate: String?,
    @Json(name = "resolve_note")
    val resolveNote: String,
    @Json(name = "is_complete")
    val isComplete: Boolean,
    @Json(name = "location")
    val location: String,

    ) {
    fun isValid(): Boolean {
        if (category.isEmpty() || status.isEmpty() || date.isEmpty() || note.isEmpty()) {
            return false
        }
        return true
    }
}