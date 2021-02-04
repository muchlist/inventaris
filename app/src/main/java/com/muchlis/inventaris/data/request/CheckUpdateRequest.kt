package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class CheckUpdateRequest(
    @Json(name = "checked_note")
    val checkedNote: String,
    @Json(name = "have_problem")
    val haveProblem: Boolean,
    @Json(name = "is_checked")
    val isChecked: Boolean,
    @Json(name = "is_resolve")
    val isResolve: Boolean
)