package com.android.dispatch.data.remote

import com.android.dispatch.model.LoginDto
import com.android.dispatch.model.LoginRequest
import com.android.dispatch.model.MailDto
import com.android.dispatch.model.ObserveMaterialRequest
import com.android.dispatch.model.PickingDto
import com.android.dispatch.model.PickingLoadRequest
import com.android.dispatch.model.StandardDto
import com.android.dispatch.model.StatusPickingRequest
import com.android.dispatch.model.StevedoresRequest
import com.android.dispatch.utils.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object Api {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(SessionManager().getBaseUrl().toString())
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
    interface ApiInterface{

        @POST("api/User")
        suspend fun authenticateCredentials(@Body request: LoginRequest): Response<LoginDto>

        @GET("api/getpicking")
        suspend fun getPicking(@Query("nropicking") nropicking: String): Response<PickingDto>

        @POST("api/pickingcarga")
        suspend fun loadPicking(@Body request: PickingLoadRequest): Response<StandardDto>

        @POST("api/estibadores")
        suspend fun stevedoresPicking(@Body request: StevedoresRequest): Response<StandardDto>

        @HTTP(method = "DELETE", path = "api/dltStevedor", hasBody = true)
        suspend fun deleteStevedor(@Body request: StevedoresRequest): Response<StandardDto>

        @PUT("api/updStevedor")
        suspend fun updateStevedor(@Body request: StevedoresRequest): Response<StandardDto>

        @GET("api/email")
        suspend fun getMails(): Response<MailDto>

        @POST("api/validar")
        suspend fun statusPicking(@Body request: StatusPickingRequest): Response<StandardDto>

        @PUT("api/obsmaterial")
        suspend fun observeMaterial(@Body request : ObserveMaterialRequest): Response<StandardDto>
    }

    fun build(): ApiInterface {
        return builder.build().create(ApiInterface::class.java)
    }
}