package com.muchlis.inventaris.data.dto

data class FindCctvDto(
    val branch: String,
    val ipAddress: String,
    val cctvName: String,
    val deactive: String
)