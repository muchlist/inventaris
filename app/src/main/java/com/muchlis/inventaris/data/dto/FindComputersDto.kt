package com.muchlis.inventaris.data.dto

data class FindComputersDto (
    val branch: String,
    val ipAddress: String,
    val clientName: String,
    val deactive: String,
    val location: String,
    val division: String,
    val seat: String
)