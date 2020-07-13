package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class ComputerDetailResponse(
    @Json(name = "author")
    val author: String,
    @Json(name = "branch")
    val branch: String,
    @Json(name = "client_name")
    val clientName: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "deactive")
    val deactive: Boolean,
    @Json(name = "division")
    val division: String,
    @Json(name = "hostname")
    val hostname: String,
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
    @Json(name = "operation_system")
    val operationSystem: String,
    @Json(name = "seat_management")
    val seatManagement: Boolean,
    @Json(name = "spec")
    val spec: Spec,
    @Json(name = "tipe")
    val tipe: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "year")
    val year: String
) {
    data class Spec(
        @Json(name = "hardisk")
        val hardisk: Int,
        @Json(name = "processor")
        val processor: Int,
        @Json(name = "ram")
        val ram: Int,
        @Json(name = "score")
        val score: Int
    )
}