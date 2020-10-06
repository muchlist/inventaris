package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class HandheldDetailResponse(
    @Json(name = "author")
    val author: String,
    @Json(name = "branch")
    val branch: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "deactive")
    val deactive: Boolean,
    @Json(name = "handheld_name")
    val handheldName: String,
    @Json(name = "_id")
    val id: String,
    @Json(name = "inventory_number")
    val inventoryNumber: String,
    @Json(name = "ip_address")
    val ipAddress: String,
    @Json(name = "last_status")
    val lastStatus: String,
    @Json(name = "location")
    val location: String,
    @Json(name = "merk")
    val merk: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "tipe")
    val tipe: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "year")
    val year: String
)