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
    val date: String,
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
    val timestamp: String = ""
)