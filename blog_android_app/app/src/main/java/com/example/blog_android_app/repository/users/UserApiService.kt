package com.example.blog_android_app.repository.users

import com.example.blog_android_app.model.UserAndToken
import com.example.blog_android_app.model.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApiService {

    @POST("/register")
    suspend fun register(
        @Body json: RequestBody
    ): Response<UserAndToken>

    @POST("/login")
    suspend fun login(
        @Body json: RequestBody
    ): Response<UserAndToken>

    @GET("/user")
    suspend fun getUser(
        @Query("id") id: Int
    ): Response<UserData>

    @Multipart
    @PUT("/user/image")
    suspend fun setImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<Void>

    @PUT("/user/update")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body json: RequestBody
    ): Response<Void>

    @HTTP(method = "DELETE", path = "/user/remove", hasBody = true)
    suspend fun deleteUser(
        @Header("Authorization") token: String,
    ): Response<Void>
}