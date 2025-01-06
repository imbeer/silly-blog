package com.example.blog_android_app.repository.comments

import com.example.blog_android_app.JSON_TYPE
import com.example.blog_android_app.model.CommentData
import com.example.blog_android_app.repository.connection.RetrofitInstance
import com.example.blog_android_app.repository.users.UserRestController
import okhttp3.RequestBody

object CommentRestController {
    private val api: CommentApiService

    init {
        val retrofit = RetrofitInstance.instance
        api = retrofit.create(CommentApiService::class.java)
    }

    suspend fun fetchComments(
        token: String = UserRestController.token.toString(),
        postId: Int,
        author: String = "",
        offset: Int,
        limit: Int
    ): List<CommentData>? {
        return api.getComments("Bearer $token", postId, author, offset, limit).body()
    }

    suspend fun createComment(
        token: String = UserRestController.token.toString(),
        commentData: CommentData
    ): CommentData? {
        val resp = api.createComment("Bearer $token", constructJson(commentData))
        return if (resp.isSuccessful) {
            resp.body()
        } else {
            null
        }
    }

    suspend fun editComment(
        token: String = UserRestController.token.toString(),
        commentData: CommentData
    ): CommentData? {
        val resp = api.editComment("Bearer $token", constructJson(commentData))
        return if (resp.isSuccessful) {
            resp.body()
        } else {
            null
        }
    }

    suspend fun deleteComment(
        token: String = UserRestController.token.toString(),
        commentData: CommentData
    ): Boolean {
        val resp = api.deleteComment("Bearer $token", constructJson(commentData))
        return (resp.code() == 204)
    }

    private fun constructJson(commentData: CommentData): RequestBody = RequestBody.create(
        JSON_TYPE,
        """{"comment": {${commentData.commentId?.let { "\"comment_id\":$it," } ?: ""}"text_content":"${commentData.textContent}", "post_id":${commentData.postId}}}"""
    )
}