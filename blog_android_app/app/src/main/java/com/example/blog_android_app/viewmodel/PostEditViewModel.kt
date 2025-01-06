package com.example.blog_android_app.viewmodel

import android.util.Log
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.posts.PostRestController
import kotlinx.coroutines.runBlocking

class PostEditViewModel(private var postData: PostData) {
    private var images: MutableList<Int> = mutableListOf()
    fun getPostData() = postData

    fun changeText(text: String) {
        postData.textContent = text
    }

    fun addImage(imageId: Int) {
        images.add(imageId)
    }

    private fun save() {
        postData.images = images
    }

    fun submit() {
        save()
        runBlocking {
            try {
                if (postData.postId != null) {
                    PostRestController.editPost(post = postData)
                } else {
                    PostRestController.createPost(post = postData)
                }
            } catch (e: Exception) {
                Log.d("TempTagPostSubmit", e.toString())
            }
        }
    }

    fun clear() {
        images.clear()
        postData.images = images
        postData.textContent = ""
    }
}