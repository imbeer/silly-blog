package com.example.blog_android_app.model

import com.google.gson.annotations.SerializedName

data class CommentData(
    @SerializedName("comment_id") val commentId: Int? = null,
    @SerializedName("post_id") var postId: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("text_content") var textContent: String = "",
    @SerializedName("author") val username: String = "",
    @SerializedName("can_be_edited") var canBeEdited: Boolean = false,
    val time: String? = null
)
