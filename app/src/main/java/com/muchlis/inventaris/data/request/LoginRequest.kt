package com.muchlis.inventaris.data.request


import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "password")
    val password: String,
    @Json(name = "username")
    val username: String
)