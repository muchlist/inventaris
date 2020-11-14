package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class HistoryEditRequest(
    @Json(name = "category")
    val category: String,
    @Json(name = "date")
    val date: String,
    @Json(name = "end_date")
    val endDate: String,
    @Json(name = "is_complete")
    val isComplete: Boolean,
    @Json(name = "location")
    val location: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "resolve_note")
    val resolveNote: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "timestamp")
    val timestamp: String
)