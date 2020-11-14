package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

class ProblemCountResponse(
    @Json(name = "issues")
    val issues: List<Issue>
) {
    data class Issue(
        @Json(name = "count")
        val count: Int,
        @Json(name = "_id")
        val id: String
    )
}