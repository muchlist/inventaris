package com.muchlis.inventaris.services

import com.muchlis.inventaris.data.request.LoginRequest
import com.muchlis.inventaris.data.response.*
import com.muchlis.inventaris.utils.App
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

    //USER
    @POST("/login")
    fun postLogin(
        @Body args: LoginRequest,
        @Header("Content-Type") contentType: String = "application/json"
    ): Call<LoginResponse>


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


    //{{url}}/api/computers?branch=banjarmasin&ip_address=&client_name=&deactive=no
    @GET("/api/computers")
    fun getComputerList(
        @Header("Authorization") token: String,
        @Query("branch") branch: String = "",
        @Query("ip_address") ipAddress: String = "",
        @Query("client_name") clientName: String = ""
    ): Call<ComputerListResponse>


    //{{url}}/api/computers/5ef05f051bbfc2b3db5d1159
    @GET("/api/computers/{id}")
    fun getComputerDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ComputerDetailResponse>

    //{{url}}/api/computers/5ef05f051bbfc2b3db5d1159
    @DELETE("/api/computers/{id}")
    fun deleteComputerDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<ResponseBody>


    //{{url}}/api/options
    @GET("/api/options")
    fun getOptions(
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    /*
    * @PUT("/api/containers/{id}")
//    fun putContainer(
//        @Path("id") id: String,*/

//    @GET("/profile")
//    fun getProfile(
//        @Header("Authorization") token: String
//    ): Call<ProfileDataResponse>
//
//    /*branch=BAGENDANG&document_level=23&agent=&page=1*/
//    @GET("/api/containers")
//    fun getContainer(
//        @Header("Authorization") token: String,
//        @Query("branch") branch: String,
//        @Query("document_level") documentLvl: Int = 0,
//        @Query("agent") agent: String,
//        @Query("search") search: String = "",
//        @Query("page") page: Int = 1
//    ): Call<ContainerListDataResponse>
//
//    @GET("/vessels")
//    fun getVessels(
//        @Header("Authorization") token: String,
//        @Query("search") search: String = ""
//    ): Call<VesselListDataResponse>
//
//    @POST("/vessels")
//    fun postVessel(
//        @Header("Authorization") token: String,
//        @Body args: InsertVesselDataInput
//    ): Call<MessageResponse>
//
//    @POST("/api/containers")
//    fun postContainer(
//        @Header("Authorization") token: String,
//        @Body args: InsertContainerDataInput
//    ): Call<MessageResponse>
//
//    @PUT("/api/containers/{id}")
//    fun putContainer(
//        @Path("id") id: String,
//        @Header("Authorization") token: String,
//        @Body args: PutContainerInfoDataInput
//    ): Call<ContainerListDataResponse.Container>
//
//    @PUT("/api/containers-change-vessel/{id}")  //AIAIAIAOSIANSIANSIUIUASIOANISNIOANS
//    fun putVesselInContainer(
//        @Path("id") id: String,
//        @Header("Authorization") token: String,
//        @Body args: PutContainerInfoVesselDataInput
//    ): Call<ContainerListDataResponse.Container>
//
//    @DELETE("/api/containers/{id}")
//    fun deleteContainer(
//        @Path("id") id: String,
//        @Header("Authorization") token: String
//    ): Call<MessageResponse>
//
//    @GET("/api/containers/{id}")
//    fun getContainerInfoDetail(
//        @Path("id") id: String,
//        @Header("Authorization") token: String
//    ): Call<ContainerListDataResponse.Container>
//
//    @POST("/api/pass-check/{containerid}/{step}/{activity}")
//    fun postContainerCheckByPass(
//        @Path("containerid") containerid: String,
//        @Path("step") step: String,
//        @Path("activity") activity: String,
//        @Header("Authorization") token: String,
//        @Body args: PostTimeStampDataInput
//    ): Call<ContainerListDataResponse.Container>
//
//    @POST("/api/create-check/{id}/{step}")
//    fun postContainerCheck(
//        @Path("id") id: String,
//        @Path("step") step: String,
//        @Header("Authorization") token: String,
//        @Body args: InsertContainerCheckDataInput
//    ): Call<ContainerCheckDataResponse>
//
//    @GET("/api/check/{id}")
//    fun getContainerCheckDetail(
//        @Path("id") id: String,
//        @Header("Authorization") token: String
//    ): Call<ContainerCheckDataResponse>
//
//    @PUT("/api/check/{id}")
//    fun putContainerCheckDetail(
//        @Path("id") id: String,
//        @Header("Authorization") token: String,
//        @Body args: PutContainerCheckDataInput
//    ): Call<ContainerCheckDataResponse>
//
//    @POST("/api/check/{id}/witness-approve")
//    fun postDocumentWitnessReady(
//        @Path("id") id: String,
//        @Header("Authorization") token: String,
//        @Body args: PostTimeStampDataInput
//    ): Call<ContainerCheckDataResponse>
//
//    @POST("/api/check/{id}/ready")
//    fun postDocumentReady(
//        @Path("id") id: String,
//        @Header("Authorization") token: String,
//        @Body args: PostTimeStampDataInput
//    ): Call<ContainerCheckDataResponse>
//
//    @POST("/api/check/{id}/approval")
//    fun postDocumentApproval(
//        @Path("id") id: String,
//        @Header("Authorization") token: String,
//        @Body args: PostTimeStampDataInput
//    ): Call<ContainerCheckDataResponse>
//
//    @POST("/api/check/{id}/unready")
//    fun postDocumentUnready(
//        @Path("id") id: String,
//        @Header("Authorization") token: String,
//        @Body args: PostTimeStampDataInput
//    ): Call<ContainerCheckDataResponse>
//
//    @GET("/api/checks")
//    fun getDocumentCheckList(
//        @Header("Authorization") token: String,
//        @Query("branch") branch: String
//    ): Call<ContainerCheckListDataResponse>
//
//
//    @Multipart
//    @POST("/api/upload/image/{id}/{position}")
//    fun postUploadImage(
//        @Path("id") id: String,
//        @Path("position") position: String,
//        @Header("Authorization") token: String,
//        //@Part image: MultipartBody.Part,
//        @Part("image\"; filename=\"pp.jpg\" ") image: RequestBody
//    ): Call<MessageResponse>
//
//    @Multipart
//    @POST("/api/upload/profil-image")
//    fun postUploadProfileImage(
//        @Header("Authorization") token: String,
//        @Part("image\"; filename=\"pp.jpg\" ") image: RequestBody
//    ): Call<MessageResponse>
//
//
//    @GET("/api/copy-photo-to/{id}")
//    fun copyImageFromAnotherCheck(
//        @Path("id") id: String,
//        @Header("Authorization") token: String
//    ): Call<ContainerCheckDataResponse>
//
//    @GET("/all-agent")
//    fun getAgentList(
//        @Header("Authorization") token: String
//    ): Call<AgentListDataResponse>
//
//    @POST("/api/container-check-reports")
//    fun postFindContainerCheckReport(
//        @Header("Authorization") token: String,
//        @Body args: GetReportContainerCheckInput
//    ): Call<MessageResponse>
//
//    @POST("/api/container-info-reports")
//    fun postFindContainerInfoReport(
//        @Header("Authorization") token: String,
//        @Body args: GetReportContainerInfoInput
//    ): Call<MessageResponse>

}

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}