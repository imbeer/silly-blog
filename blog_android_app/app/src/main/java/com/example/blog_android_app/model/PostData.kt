package com.example.blog_android_app.model

data class PostData(
    val post_id: Int? = null,
    val user_id: Int? = null,
    val author: String = "",
    var text_content: String = "",
    val likes: Int = 0,
    var isLiked: Boolean = false,
    var images: List<Int>? = null,
    val time: String? = null
)
