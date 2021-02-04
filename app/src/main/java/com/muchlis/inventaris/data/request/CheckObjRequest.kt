package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class CheckObjRequest(
    @Json(name = "location")
    val location: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "shifts")
    val shifts: List<Int>,
    @Json(name = "type")
    val type: String
)