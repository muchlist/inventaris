package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class HandheldListResponse(
    @Json(name = "handhelds")
    val handhelds: List<Handheld>
) {
    data class Handheld(
        @Json(name = "branch")
        val branch: String,
        @Json(name = "handheld_name")
        val handheldName: String,
        @Json(name = "_id")
        val id: String,
        @Json(name = "ip_address")
        val ipAddress: String,
        @Json(name = "last_status")
        val lastStatus: String,
        @Json(name = "location")
        val location: String
    )
}