package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class PelindoAppsListResponse(
    @Json(name = "apps")
    val apps: List<App>
) {
    data class App(
        @Json(name = "apps_name")
        val appsName: String,
        @Json(name = "_id")
        val id: String
    )
}