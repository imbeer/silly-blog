package com.example.blog_android_app.repository

import com.example.blog_android_app.BASE_URL
import com.example.blog_android_app.TEST_JWT
import com.example.blog_android_app.model.PostData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PostRestController {
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
    ): List<PostData>? {
        return api.getPosts("Bearer $token", author, prompt, offset, limit).body()
    }

    suspend fun createPost(
        token: String = TEST_JWT,
        textContent: String = "",
        images: List<Int>
    ): PostData {
        val post = PostData(text_content = textContent, images = images)
        return api.createPost(token, post)
    }
}