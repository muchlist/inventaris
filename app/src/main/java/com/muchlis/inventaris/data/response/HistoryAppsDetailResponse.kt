package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class HistoryAppsDetailResponse(
    @Json(name = "author")
    val author: String,
    @Json(name = "author_id")
    val authorId: String,
    @Json(name = "branch")
    val branch: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "desc")
    val desc: String,
    @Json(name = "duration_minute")
    val durationMinute: Int,
    @Json(name = "end_date")
    val endDate: String?,
    @Json(name = "_id")
    val id: String,
    @Json(name = "is_complete")
    val isComplete: Boolean,
    @Json(name = "location")
    val location: String,
    @Json(name = "parent_id")
    val parentId: String,
    @Json(name = "parent_name")
    val parentName: String,
    @Json(name = "pic")
    val pic: String,
    @Json(name = "resolve_note")
    val resolveNote: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "updated_at")
    val updatedAt: String
)