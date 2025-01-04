package com.example.blog_android_app.repository.likes

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PUT

interface LikeApiService {
    @PUT("/post/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Body json: RequestBody
    ): Response<String>

    @HTTP(method = "DELETE", path = "/post/like", hasBody = true)
    suspend fun unlikePost(
        @Header("Authorization") token: String,
        @Body json: RequestBody
    ): Response<Void>
}