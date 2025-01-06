package com.example.blog_android_app.repository.users

import com.example.blog_android_app.model.UserAndToken
import com.example.blog_android_app.model.UserData
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}