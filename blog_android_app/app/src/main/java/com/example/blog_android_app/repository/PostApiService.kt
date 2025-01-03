package com.example.blog_android_app.repository

import com.example.blog_android_app.model.PostData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PostApiService {
    @GET("posts")
    suspend fun getPosts(
        @Header("Authorization") token: String,
        @Query("author") author: String,
        @Query("prompt") prompt: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<PostData>>

    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body post: PostData
    ): PostData
}