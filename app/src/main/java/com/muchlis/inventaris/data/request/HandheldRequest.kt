package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class HandheldRequest(
    @Json(name = "deactive")
    val deactive: Boolean,
    @Json(name = "handheld_name")
    val handheldName: String,
    @Json(name = "inventory_number")
    val inventoryNumber: String,
    @Json(name = "ip_address")
    val ipAddress: String,
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
    @Json(name = "year")
    val year: String
)