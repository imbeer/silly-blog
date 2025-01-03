package com.example.blog_android_app.repository.likes

import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.connection.RetrofitInstance

object LikeRestController {
    private val api: LikeApiService

    init {
        val retrofit = RetrofitInstance.instance
        api = retrofit.create(LikeApiService::class.java)
    }

    fun likePost(token: String, postData: PostData) {
        val post = LikeApiService.LikablePost(postData.post_id!!)
        api.likePost("Bearer $token", post)
    }

    fun unlikePost(token: String, postData: PostData) {
        val post = LikeApiService.LikablePost(postData.post_id!!) // its always not null if you didn't create post by thyself
        api.unlikePost("Bearer $token", post)
    }
}