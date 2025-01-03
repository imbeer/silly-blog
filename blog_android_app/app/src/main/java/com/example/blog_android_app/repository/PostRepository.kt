package com.example.blog_android_app.repository

import com.example.blog_android_app.BASE_URL
import com.example.blog_android_app.TEST_JWT
import com.example.blog_android_app.model.PostData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PostApiService {
    @GET("posts")
    suspend fun getPosts(
        @Header("Authorization") token: String,
        @Query("author") author: String,
        @Query("prompt") prompt: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): List<PostData>
}

object PostRepository {
    private val api: PostApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(PostApiService::class.java)
    }

    suspend fun fetchPosts(
        token: String = TEST_JWT,
        author: String = "",
        prompt: String = "",
        offset: Int,
        limit: Int
    ): List<PostData> {
        return api.getPosts("Bearer $token", author, prompt, offset, limit)
    }
}