package com.example.blog_android_app.model

import com.google.gson.annotations.SerializedName

data class CommentData(
    @SerializedName("comment_id") val commentId: Int? = null,
    @SerializedName("post_id") val postId: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("text_content") val textContent: String = "",
    @SerializedName("author") val username: String = "",
    var canBeEdited: Boolean = false,
    val time: String? = null
)
