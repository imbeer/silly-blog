package com.example.blog_android_app

data class PostItem(
    val username: String,
    val text: String,
    val imageUrls: List<String>? = null,
    var like: Boolean = false
)
