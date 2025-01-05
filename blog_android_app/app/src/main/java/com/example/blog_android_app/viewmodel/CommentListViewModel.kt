package com.example.blog_android_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.blog_android_app.DEFAULT_COMMENT_LOAD_LIMIT
import com.example.blog_android_app.model.PostData

class CommentListViewModel(
    private val postData: PostData,
    private val author: String = "",
    private val loadLimit: Int = DEFAULT_COMMENT_LOAD_LIMIT
) : ViewModel() {

}