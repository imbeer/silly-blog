package com.example.blog_android_app.model

data class PostData(
    val post_id: Int,
    val user_id: Int,
    val author: String,
    val text_content: String,
    val likes: Int,
    var isLiked: Boolean,
    val images: List<Int>?,
    val time: String
)
