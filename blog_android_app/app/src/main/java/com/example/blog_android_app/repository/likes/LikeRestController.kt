package com.example.blog_android_app.repository.likes

import android.util.Log
import com.example.blog_android_app.TEST_JWT
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.connection.RetrofitInstance

object LikeRestController {
    private val api: LikeApiService

    init {
        val retrofit = RetrofitInstance.instance
        api = retrofit.create(LikeApiService::class.java)
    }

    // todo: Idk Im tired fuck it
    fun likePost(token: String = TEST_JWT, postData: PostData) {
        Log.d("TempTagLikePost", constructJson(postData))
        val call = api.likePost("Bearer $token", constructJson(postData))
        try {
            val response = call.execute()
            if (response.isSuccessful) postData.isLiked = true
            else Log.d("TempTagLikePost", "not liked")
        } catch (e: Exception) {
            Log.d("TempTagLikePost", e.toString())
        }
    }

    fun unlikePost(token: String = TEST_JWT, postData: PostData) {
        Log.d("TempTagDisLikePost", constructJson(postData))
        api.unlikePost("Bearer $token", constructJson(postData))
    }

    private fun constructJson(postData: PostData): String = """{"post":{"post_id":${postData.post_id}}}"""
}