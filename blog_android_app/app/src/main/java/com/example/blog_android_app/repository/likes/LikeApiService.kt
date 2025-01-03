package com.example.blog_android_app.repository.likes

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PUT

interface LikeApiService {
    @PUT("/post/like")
    fun likePost(
        @Header("Authorization") token: String,
        @Body json: String
    ): Call<String>

    @HTTP(method = "DELETE", path = "/post/like", hasBody = true)
    fun unlikePost(
        @Header("Authorization") token: String,
        @Body json: String
    ): Call<Void>

}