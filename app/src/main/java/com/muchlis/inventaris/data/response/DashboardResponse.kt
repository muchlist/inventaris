package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class DashboardResponse(
    @Json(name = "histories")
    val histories: List<HistoryResponse>,
    @Json(name = "issues")
    val issues: List<Issue>,
    @Json(name = "option_lvl")
    val optionLvl: Int
){
    data class Issue(
        @Json(name = "count")
        val count: Int,
        @Json(name = "_id")
        val id: String
    )
}