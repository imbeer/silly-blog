package com.example.blog_android_app.repository.posts

import com.example.blog_android_app.JSON_TYPE
import com.example.blog_android_app.TEST_JWT
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.connection.RetrofitInstance
import okhttp3.RequestBody

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
        post: PostData
    ): PostData {
        return api.createPost(token, constructJson(post))
    }

    private fun constructJson(postData: PostData): RequestBody = RequestBody.create(
            JSON_TYPE,
            """{"post": {${postData.post_id?.let { "\"post_id\":$it," } ?: ""}"text_content": "${postData.text_content}","images": ${(postData.images ?: emptyList()).joinToString(prefix = "[", postfix = "]")}}}"""
        )
}