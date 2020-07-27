package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class ComputerEditRequest(
    @Json(name = "client_name")
    val clientName: String,
    @Json(name = "deactive")
    val deactive: Boolean,
    @Json(name = "division")
    val division: String,
    @Json(name = "hardisk")
    val hardisk: Int,
    @Json(name = "hostname")
    val hostname: String,
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
    @Json(name = "operation_system")
    val operationSystem: String,
    @Json(name = "processor")
    val processor: String,
    @Json(name = "ram")
    val ram: Int,
    @Json(name = "seat_management")
    val seatManagement: Boolean,
    @Json(name = "timestamp")
    val timestamp: String,
    @Json(name = "tipe")
    val tipe: String,
    @Json(name = "year")
    val year: String
)