package com.muchlis.inventaris.data.dto

data class FindHistoryDto(
    val branch: String,
    val category: String,
    val limit: Int,
//    val isComplete: Int = 100,
    /*Versi 3 ada tambahan completeStatus untuk menggantikan isComplete
    * 0 progress
    * 1 pending
    * 2 complete
    * */
    val completeStatus: Int = -1,
)