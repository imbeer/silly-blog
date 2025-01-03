package com.example.blog_android_app.repository.posts

import com.example.blog_android_app.TEST_JWT
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.connection.RetrofitInstance

object PostRestController {
    private val api: PostApiService

    init {
        val retrofit = RetrofitInstance.instance
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