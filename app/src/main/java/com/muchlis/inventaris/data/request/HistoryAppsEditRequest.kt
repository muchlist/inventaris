package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class HistoryAppsEditRequest(
    @Json(name = "desc")
    val desc: String,
    @Json(name = "end_date")
    val endDate: String?,
    @Json(name = "is_complete")
    val isComplete: Boolean,
    @Json(name = "location")
    val location: String,
    @Json(name = "pic")
    val pic: String,
    @Json(name = "resolve_note")
    val resolveNote: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "timestamp")
    val timestamp: String,
    @Json(name = "title")
    val title: String
)