package com.muchlis.inventaris.services

import com.muchlis.inventaris.data.request.*
import com.muchlis.inventaris.data.response.*
import com.muchlis.inventaris.utils.App
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private var BASE_URL = App.prefs.baseUrl

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {

    /* LOGIN -------------------------------------------------------------------
    */

    //USER
    @POST("/login")
    fun postLogin(
        @Body args: LoginRequest,
        @Header("Content-Type") contentType: String = "application/json"
    ): Call<LoginResponse>


    /* HISTORY -------------------------------------------------------------------
    */

    //{{url}}/api/histories/5ef05f051bbfc2b3db5d1159
    @POST("/api/histories/{id}")
    fun postHistory(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body args: HistoryRequest
    ): Call<HistoryResponse>


    //{{url}}/api/histories?branch=BANJARMASIN&category=&limit=3
    @GET("/api/histories")
    fun getHistory(
        @Header("Authorization") token: String,
        @Query("branch") branch: String = "",
        @Query("category") category: String = "",
        @Query("limit") limit: Int?
    ): Call<HistoryListResponse>


    //{{url}}/api/histories/5ef05f051bbfc2b3db5d1159
    @GET("/api/histories/{id}")
    fun getHistoryFromParent(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<HistoryListResponse>

    /* COMPUTER -------------------------------------------------------------------
    */

    //{{url}}/api/computers
    @POST("/api/computers")
    fun postComputer(
        @Header("Authorization") token: String,
        @Body args: ComputerRequest
    ): Call<ComputerDetailResponse>

    //{{url}}/api/computers?branch=banjarmasin&ip_address=&client_name=&deactive=no/yes
    @GET("/api/computers")
    fun getComputerList(
        @Header("Authorization") token: String,
        @Query("branch") branch: String = "",
        @Query("location") location: String = "",
        @Query("division") division: String = "",
        @Query("ip_address") ipAddress: String = "",
        @Query("client_name") clientName: String = "",
        @Query("deactive") deactive: String = "", //yes no
        @Query("seat") seat: String = "" //yes no
    ): Call<ComputerListResponse>


    //{{url}}/api/computers/5ef05f051bbfc2b3db5d1159
    @GET("/api/computers/{id}")
    fun getComputerDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ComputerDetailResponse>

    //{{url}}/api/computers/5ef05f051bbfc2b3db5d1159
    @PUT("/api/computers/{id}")
    fun editComputerDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body args: ComputerEditRequest
    ): Call<ComputerDetailResponse>

    //{{url}}/api/computers/5ef05f051bbfc2b3db5d1159
    @DELETE("/api/computers/{id}")
    fun deleteComputerDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ResponseBody>


    //{{url}}/api/delete-history/5ef2d20fd72e1f6c75093643
    @DELETE("/api/delete-history/{id}")
    fun deleteComputerHistory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ResponseBody>


    //{{url}}/api/computers/5ef2d20fd72e1f6c75093643/(active atau deactive)
    @POST("/api/computers/{id}/{active}")
    fun changeStatusActive(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Path("active") active: String,
        @Body args: JustTimeStampRequest
    ): Call<ComputerDetailResponse>

    /* STOCK -------------------------------------------------------------------
    */

    //{{url}}/api/stock?branch=banjarmasin&ip_address=&stock_name=&deactive=no
    @GET("/api/stocks")
    fun getStockList(
        @Header("Authorization") token: String,
        @Query("branch") branch: String = "",
        @Query("stock_name") stockName: String = "",
        @Query("location") location: String = "",
        @Query("category") category: String = "",
        @Query("deactive") deactive: String = "" //yes no
    ): Call<StockListResponse>

    //{{url}}/api/stocks
    @POST("/api/stocks")
    fun postStock(
        @Header("Authorization") token: String,
        @Body args: StockRequest
    ): Call<StockDetailResponse>

    //{{url}}/api/computers/5ef05f051bbfc2b3db5d1159
    @PUT("/api/stocks/{id}")
    fun editStockDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body args: StockEditRequest
    ): Call<StockDetailResponse>

    //{{url}}/api/stocks/5ef4025f59fce41c604218f5
    @GET("/api/stocks/{id}")
    fun getStockDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<StockDetailResponse>

    //{{url}}/api/stocks/5ef4025f59fce41c604218f5
    @DELETE("/api/stocks/{id}")
    fun deleteStockDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ResponseBody>

    //{{url}}/api/stocks/5ef2d20fd72e1f6c75093643/(active atau deactive)
    @POST("/api/stocks/{id}/{active}")
    fun changeStatusActiveStock(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Path("active") active: String,
        @Body args: JustTimeStampRequest
    ): Call<StockDetailResponse>

    //{{url}}/api/use-stock/5ef4025f59fce41c604218f5
    @POST("/api/use-stock/{id}")
    fun useStock(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body args: StockUseRequest
    ): Call<StockDetailResponse>

    @Multipart
    @POST("/api/stocks/{id}/upload")
    fun uploadImageStock(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("image\"; filename=\"pp.jpg\" ") image: RequestBody
    ): Call<StockDetailResponse>


    /* CCTV -------------------------------------------------------------------
    */
    //{{url}}/api/cctvs?branch=banjarmasin&ip_address=&cctv_name=&deactive=no
    @GET("/api/cctvs")
    fun getCctvList(
        @Header("Authorization") token: String,
        @Query("branch") branch: String = "",
        @Query("location") location: String = "",
        @Query("ip_address") ipAddress: String = "",
        @Query("cctv_name") cctvName: String = "",
        @Query("last_ping") lastPing: String = "", //UP DOWN
        @Query("deactive") deactive: String = "" //yes no
    ): Call<CctvListResponse>

    //{{url}}/api/cctvs/5ef05f051bbfc2b3db5d1159
    @GET("/api/cctvs/{id}")
    fun getCctvDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<CctvDetailResponse>

    //{{url}}/api/stocks/5ef2d20fd72e1f6c75093643/(active atau deactive)
    @POST("/api/cctvs/{id}/{active}")
    fun changeStatusActiveCctv(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Path("active") active: String,
        @Body args: JustTimeStampRequest
    ): Call<CctvDetailResponse>

    //{{url}}/api/cctvs/5ef05f051bbfc2b3db5d1159
    @DELETE("/api/cctvs/{id}")
    fun deleteCctvDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ResponseBody>

    //{{url}}/api/cctvs
    @POST("/api/cctvs")
    fun postCctv(
        @Header("Authorization") token: String,
        @Body args: CctvRequest
    ): Call<CctvDetailResponse>

    //{{url}}/api/cctvs/5ef05f051bbfc2b3db5d1159
    @PUT("/api/cctvs/{id}")
    fun editCctvDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body args: CctvEditRequest
    ): Call<CctvDetailResponse>

    @Multipart
    @POST("/api/cctvs/{id}/upload")
    fun uploadImageCctv(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("image\"; filename=\"pp.jpg\" ") image: RequestBody
    ): Call<CctvDetailResponse>


    /*Pelindo Apps---------------------------------------------------------------
    * */

    //{{url}}/api/apps?apps_name=
    @GET("/api/apps")
    fun getPelindoAppsList(
        @Header("Authorization") token: String,
        @Query("apps_name") appsName: String = ""
    ): Call<PelindoAppsListResponse>


    //{{url}}/api/apps/5ef05f051bbfc2b3db5d1159
    @GET("/api/apps/{id}")
    fun getAppsDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<PelindoAppsDetailResponse>

    /*Pelindo Apps History------------------------------------------------------
    * */

    //{{url}}/api/apps/5f5e601f7c8a9c2e97b137b4/histories
    @POST("/api/apps/{id}/histories")
    fun createAppsHistoryForParent(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body args: HistoryAppsRequest,
    ): Call<ErrorResponse>

    //{{url}}/api/apps-histories?app_name=SPINER&branch=&category=&limit=100
    @GET("/api/apps-histories")
    fun getPelindoAppsHistoryList(
        @Header("Authorization") token: String,
        @Query("app_name") appName: String = "",
        @Query("branch") branch: String = "",
        @Query("limit") limit: Int = 100,
    ): Call<HistoryAppsListResponse>

    //{{url}}/api/apps/5f584d5aec15b175ecc37dbd/histories
    @GET("/api/apps/{id}/histories")
    fun getPelindoAppsHistoryListByParent(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Call<HistoryAppsListResponse>

    //{{url}}/api/app-histories/5f5e61ed7c8a9c2e97b137b5
    @GET("/api/app-histories/{id}")
    fun getAppsHistoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<HistoryAppsDetailResponse>

    //{{url}}/api/app-histories/5f599ce236f8c28239364355
    @PUT("/api/app-histories/{id}")
    fun editAppsHistoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body args: HistoryAppsEditRequest,
    ): Call<HistoryAppsDetailResponse>

    //{{url}}/api/app-histories/5f5995ca0f62394bddfb2de9
    @DELETE("/api/app-histories/{id}")
    fun deletePelindoAppsHistory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ResponseBody>


    /* OPTION -------------------------------------------------------------------
    */

    //{{url}}/api/options
    @GET("/api/options")
    fun getOptions(
        @Header("Authorization") token: String
    ): Call<ResponseBody>

}

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}