package com.example.blog_android_app.repository.comments

import com.example.blog_android_app.model.CommentData
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CommentApiService {
    @GET("/post")
    suspend fun getComments(
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Query("author") author: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<CommentData>>

    @POST("/post/comment")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Body json: RequestBody
    ): Response<CommentData>

    @HTTP(method = "DELETE", path = "/post/comment", hasBody = true)
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Body json: RequestBody
    ): Response<Void>

    @PUT("/post/comment")
    suspend fun editComment(
        @Header("Authorization") token: String,
        @Body json: RequestBody
    ): Response<CommentData>
}