package com.muchlis.inventaris.data.response


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CctvDetailResponse(
    @Json(name = "author")
    val author: String,
    @Json(name = "branch")
    val branch: String,
    @Json(name = "cctv_name")
    val cctvName: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "deactive")
    val deactive: Boolean,
    @Json(name = "_id")
    val id: String,
    @Json(name = "inventory_number")
    val inventoryNumber: String,
    @Json(name = "ip_address")
    val ipAddress: String,
    @Json(name = "last_ping")
    val lastPing: String,
    @Json(name = "last_status")
    val lastStatus: String,
    @Json(name = "location")
    val location: String,
    @Json(name = "merk")
    val merk: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "ping_state")
    val pingState: List<PingState>,
    @Json(name = "tipe")
    val tipe: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "year")
    val year: String
): Parcelable {
    @Parcelize
    data class PingState(
        @Json(name = "code") //0 down 1half 2up
        val code: Int,
        @Json(name = "status")
        val status: String,
        @Json(name = "time_date")
        val timeDate: String
    ) : Parcelable
}