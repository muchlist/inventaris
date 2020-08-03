package com.muchlis.inventaris.data.dto

data class FindStocksDto (
    val branch: String,
    val stockName: String,
    val deactive: String,
    val location: String,
    val category: String
)