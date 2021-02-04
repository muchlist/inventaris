package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class CheckObjResponse(
    @Json(name = "check-obj")
    val checkObj: List<CheckObj>
) {
    data class CheckObj(
        @Json(name = "branch")
        val branch: String,
        @Json(name = "checked_note")
        val checkedNote: String,
        @Json(name = "created_at")
        val createdAt: String,
        @Json(name = "have_problem")
        val haveProblem: Boolean,
        @Json(name = "_id")
        val id: String,
        @Json(name = "is_resolve")
        val isResolve: Boolean,
        @Json(name = "location")
        val location: String,
        @Json(name = "name")
        val name: String,
        @Json(name = "note")
        val note: String,
        @Json(name = "shifts")
        val shifts: List<Int>,
        @Json(name = "type")
        val type: String,
        @Json(name = "updated_at")
        val updatedAt: String
    )
}