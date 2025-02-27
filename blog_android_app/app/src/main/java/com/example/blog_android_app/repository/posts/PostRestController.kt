package com.example.blog_android_app.repository.posts

import com.example.blog_android_app.JSON_TYPE
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.connection.RetrofitInstance
import com.example.blog_android_app.repository.users.UserRestController
import okhttp3.RequestBody

object PostRestController {
    private val api: PostApiService

    init {
        val retrofit = RetrofitInstance.instance
        api = retrofit.create(PostApiService::class.java)
    }

    suspend fun fetchPosts(
        token: String = UserRestController.token.toString(),
        sort: Int = 1,
        author: Int = -1,
        prompt: String = "",
        offset: Int,
        limit: Int
    ): List<PostData>? {
        return api.getPosts("Bearer $token", sort, author, prompt, offset, limit).body()
    }

    suspend fun createPost(
        token: String = UserRestController.token.toString(),
        post: PostData
    ): PostData? {
        val resp = api.createPost("Bearer $token", constructJson(post))
        return if (resp.isSuccessful) {
            resp.body()?.post
        } else {
            null
        }
    }

    suspend fun editPost(
        token: String = UserRestController.token.toString(),
        post: PostData
    ): PostData? {
        val resp = api.editPost("Bearer $token", constructJson(post))
        return if (resp.isSuccessful) {
            resp.body()?.post
        } else {
            null
        }
    }

    suspend fun deletePost(
        token: String = UserRestController.token.toString(),
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