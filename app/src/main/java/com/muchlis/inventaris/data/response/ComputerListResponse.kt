package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class ComputerListResponse(
    @Json(name = "computers")
    val computers: List<Computer>
) {
    data class Computer(
        @Json(name = "branch")
        val branch: String,
        @Json(name = "client_name")
        val clientName: String,
        @Json(name = "division")
        val division: String,
        @Json(name = "location")
        val location: String,
        @Json(name = "ip_address")
        val ipAddress: String,
        @Json(name = "_id")
        val id: String,
        @Json(name = "last_status")
        val lastStatus: String,
        @Json(name = "seat_management")
        val seatManagement: Boolean,
        val case: List<Case>,
        @Json(name = "case_size")
        val caseSize: Int,
    )
}