package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class CheckListResponse(
    @Json(name = "check")
    val check: List<Check>
) {
    data class Check(
        @Json(name = "branch")
        val branch: String,
        @Json(name = "created_at")
        val createdAt: String,
        @Json(name = "created_by")
        val createdBy: String,
        @Json(name = "_id")
        val id: String,
        @Json(name = "is_finish")
        val isFinish: Boolean,
        @Json(name = "shift")
        val shift: Int,
        @Json(name = "updated_at")
        val updatedAt: String
    )
}