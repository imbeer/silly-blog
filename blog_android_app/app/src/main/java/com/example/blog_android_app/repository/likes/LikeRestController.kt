package com.example.blog_android_app.repository.likes

import com.example.blog_android_app.JSON_TYPE
import com.example.blog_android_app.TEST_JWT
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.connection.RetrofitInstance
import kotlinx.coroutines.runBlocking
import okhttp3.RequestBody

object LikeRestController {
    private val api: LikeApiService

    init {
        val retrofit = RetrofitInstance.instance
        api = retrofit.create(LikeApiService::class.java)
    }

    fun chaneLikeStatus(postData: PostData) {
        if (postData.isLiked) {
            runBlocking {
                unlikePost(postData = postData)
            }
        } else {
            runBlocking {
                likePost(postData = postData)
            }
        }
    }

    private suspend fun likePost(token: String = TEST_JWT, postData: PostData) {
        val response = api.likePost("Bearer $token", constructJson(postData))
        if (response.isSuccessful) {
            postData.isLiked = true
            postData.likes++ // todo: get from backend
        }
    }

    private suspend fun unlikePost(token: String = TEST_JWT, postData: PostData) {
        val response = api.unlikePost("Bearer $token", constructJson(postData))
        if (response.isSuccessful) {
            postData.isLiked = false
            postData.likes-- // todo: get from backend
        }
    }

    private fun constructJson(postData: PostData): RequestBody =
        RequestBody.create(
            JSON_TYPE,
            """
            {
                "post": {
                    "post_id": ${postData.postId}
                }
            }
            """)
}