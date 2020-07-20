package com.muchlis.inventaris.data.dto

data class FindHistoryDto (
    val branch: String,
    val category: String,
    val limit: Int
)