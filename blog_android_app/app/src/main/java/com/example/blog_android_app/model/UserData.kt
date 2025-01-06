package com.example.blog_android_app.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("image_link") var imageLink: String = "",
    var username: String? = null,
    var bio: String = "",
    val email: String = "",
    val password: String = ""
)

data class UserAndToken(
    val user: UserData,
    val token: String
)
