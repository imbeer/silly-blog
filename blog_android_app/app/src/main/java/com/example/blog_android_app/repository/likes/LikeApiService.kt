package com.example.blog_android_app.repository.likes

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT

interface LikeApiService {
    @PUT("/post/like")
    fun likePost(
        @Header("Authorization") token: String,
        @Body post: LikablePost
    ): Call<Void>

    @DELETE("/post/like")
    fun unlikePost(
        @Header("Authorization") token: String,
        @Body post: LikablePost
    ): Call<Void>

    data class LikablePost(
        val post_id: Int
    )
}