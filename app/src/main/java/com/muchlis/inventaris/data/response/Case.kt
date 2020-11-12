package com.muchlis.inventaris.data.response

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Case(
    @Json(name = "case_id")
    val caseId: String,
    @Json(name = "case_note")
    val caseNote: String
): Parcelable