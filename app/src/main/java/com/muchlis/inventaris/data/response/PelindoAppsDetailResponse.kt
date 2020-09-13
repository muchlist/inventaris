package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class PelindoAppsDetailResponse(
    @Json(name = "apps_name")
    val appsName: String,
    @Json(name = "branches")
    val branches: List<String>,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "_id")
    val id: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "programmers")
    val programmers: List<String>,
    @Json(name = "trouble_count")
    val troubleCount: Int,
    @Json(name = "updated_at")
    val updatedAt: String
)