package com.muchlis.inventaris.data.response

import com.squareup.moshi.Json

data class HistoryResponse(
    @Json(name = "author")
    val author: String,
    @Json(name = "branch")
    val branch: String,
    @Json(name = "category")
    val category: String,
    @Json(name = "date")
    val date: String = "2020-10-00 08:00:00.625000" ,
    @Json(name = "_id")
    val id: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "parent_id")
    val parentId: String,
    @Json(name = "parent_name")
    val parentName: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "timestamp")
    val timestamp: String = "",

    /*Atas versi 1, bawah tambahan untuk versi 2*/

    @Json(name = "author_id")
    val authorId: String = "",
    @Json(name = "created_at")
    val createdAt: String = "2020-10-00 08:00:00.625000",
    @Json(name = "duration_minute")
    val durationMinute: Int = 0,
    @Json(name = "end_date")
    val endDate: String? = null,
    @Json(name = "is_complete")
    val isComplete: Boolean = false,
    @Json(name = "location")
    val location: String = "",
    @Json(name = "resolve_note")
    val resolveNote: String = "",
    @Json(name = "updated_by")
    val updatedBy: String = "",
    @Json(name = "updated_by_id")
    val updatedById: String = ""
)