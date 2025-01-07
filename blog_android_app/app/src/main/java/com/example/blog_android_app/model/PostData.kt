package com.example.blog_android_app.model

import com.google.gson.annotations.SerializedName

data class PostData(
    @SerializedName("post_id") val postId: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("text_content") var textContent: String = "",
    @SerializedName("author") val username: String = "",
    var likes: Int = 0,
    var isLiked: Boolean = false,
    var canBeEdited: Boolean = false,
    var images: List<Int>? = null,
    val time: String? = null
)

data class PostDataFromJson(
    @SerializedName("post") val post: PostData
)
