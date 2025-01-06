package com.example.blog_android_app.repository.users

import com.example.blog_android_app.model.UserAndToken
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {

    @POST("/register")
    suspend fun register(
        @Body json: RequestBody
    ): Response<UserAndToken>

    @POST("/login")
    suspend fun login(
        @Body json: RequestBody
    ): Response<UserAndToken>
}