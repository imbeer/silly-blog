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
    ): PostData? {
        val resp = api.createPost("Bearer $token", constructJson(post))
        return if (resp.isSuccessful) {
            resp.body()
        } else {
            null
        }
    }

    suspend fun deletePost(
        token: String = TEST_JWT,
        post: PostData
    ): Boolean {
        val resp = api.deletePost("Bearer $token", constructJson(post))
        return (resp.code() == 204)
    }

    private fun constructJson(postData: PostData): RequestBody = RequestBody.create(
            JSON_TYPE,
            """{"post": {${postData.postId?.let { "\"post_id\":$it," } ?: ""}"text_content": "${postData.textContent}","images": ${(postData.images ?: emptyList()).joinToString(prefix = "[", postfix = "]")}}}"""
        )
}