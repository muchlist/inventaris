package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class CheckResponse(
    @Json(name = "branch")
    val branch: String,
    @Json(name = "checks_obj")
    val checksObj: List<ChecksObj>,
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
) {
    data class ChecksObj(
        @Json(name = "checked_at")
        val checkedAt: String?,
        @Json(name = "checked_note")
        val checkedNote: String,
        @Json(name = "have_problem")
        val haveProblem: Boolean,
        @Json(name = "id")
        val id: String,
        @Json(name = "image_path")
        val imagePath: String,
        @Json(name = "is_checked")
        val isChecked: Boolean,
        @Json(name = "is_resolve")
        val isResolve: Boolean,
        @Json(name = "location")
        val location: String,
        @Json(name = "name")
        val name: String,
        @Json(name = "type")
        val type: String
    )
}