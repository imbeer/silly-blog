package com.example.blog_android_app.model

import com.google.gson.annotations.SerializedName

data class PostData(
    @SerializedName("post_id") val postId: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    val author: String = "",
    @SerializedName("text_content") var textContent: String = "",
    val likes: Int = 0,
    var isLiked: Boolean = false,
    var images: List<Int>? = null,
    val time: String? = null
)
