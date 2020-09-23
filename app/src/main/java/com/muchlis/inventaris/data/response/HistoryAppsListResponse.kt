package com.muchlis.inventaris.data.response


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class HistoryAppsListResponse(
    @Json(name = "histories")
    val histories: List<History>
) {
    @Parcelize
    data class History(
        @Json(name = "author")
        val author: String,
        @Json(name = "branch")
        val branch: String,
        @Json(name = "desc")
        val desc: String,
        @Json(name = "duration_minute")
        val durationMinute: Int,
        @Json(name = "_id")
        val id: String,
        @Json(name = "is_complete")
        val isComplete: Boolean,
        @Json(name = "parent_name")
        val parentName: String,
        @Json(name = "resolve_note")
        val resolveNote: String,
        @Json(name = "start_date")
        val startDate: String,
        @Json(name = "status")
        val status: String,
        @Json(name = "title")
        val title: String,
        @Json(name = "updated_at")
        val updatedAt: String,
    ): Parcelable
}