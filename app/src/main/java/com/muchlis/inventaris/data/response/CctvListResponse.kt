package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class CctvListResponse(
    @Json(name = "cctvs")
    val cctvs: List<Cctv>
) {
    data class Cctv(
        @Json(name = "branch")
        val branch: String,
        @Json(name = "cctv_name")
        val cctvName: String,
        @Json(name = "_id")
        val id: String,
        @Json(name = "ip_address")
        val ipAddress: String,
        @Json(name = "last_ping")
        val lastPing: String,
        @Json(name = "last_status")
        val lastStatus: String,
        @Json(name = "location")
        val location: String

    )
}