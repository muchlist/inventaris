package com.muchlis.inventaris.data.response


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class SelectOptionResponse(
    @SerializedName("kalimantan")
    @Json(name = "kalimantan")
    val kalimantan: List<String>,
    @SerializedName("cctv_devices_type")
    @Json(name = "cctv_devices_type")
    val cctvDevicesType: List<String>,
    @SerializedName("divisions")
    @Json(name = "divisions")
    val divisions: List<String>,
    @SerializedName("hardisks")
    @Json(name = "hardisks")
    val hardisks: List<Int>,
    @SerializedName("locations")
    @Json(name = "locations")
    val locations: List<String>,
    @SerializedName("stock_location")
    @Json(name = "stock_location")
    val stockLocations: List<String>,
    @SerializedName("operation_system")
    @Json(name = "operation_system")
    val operationSystem: List<String>,
    @SerializedName("pc_devices_type")
    @Json(name = "pc_devices_type")
    val pcDevicesType: List<String>,
    @SerializedName("processors")
    @Json(name = "processors")
    val processors: List<String>,
    @SerializedName("rams")
    @Json(name = "rams")
    val rams: List<Int>,
    @SerializedName("server_category")
    @Json(name = "server_category")
    val serverCategory: List<String>,
    @SerializedName("stock_category")
    @Json(name = "stock_category")
    val stockCategory: List<String>,
    @SerializedName("history")
    @Json(name = "history")
    val history: List<String>,
    @SerializedName("app_history")
    @Json(name = "app_history")
    val appHistory: List<String>,
    @SerializedName("cctv_history")
    @Json(name = "cctv_history")
    val cctvHistory: List<String>,
    @SerializedName("version")
    @Json(name = "version")
    val version: Int
)