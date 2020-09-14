package com.muchlis.inventaris.data.dto

data class FindAppHistoryDto(
    val appName: String,
    val branch: String,
    val category: String,
    val limit: Int,
)