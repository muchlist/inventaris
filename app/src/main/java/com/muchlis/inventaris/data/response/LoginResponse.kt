package com.muchlis.inventaris.data.response


import com.squareup.moshi.Json

data class LoginResponse(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "branch")
    val branch: String,
    @Json(name = "isAdmin")
    val isAdmin: Boolean,
    @Json(name = "isEndUser")
    val isEndUser: Boolean,
    @Json(name = "name")
    val name: String
)