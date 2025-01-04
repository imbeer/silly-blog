package com.example.blog_android_app.viewmodel

import android.util.Log
import com.example.blog_android_app.TEST_JWT
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.posts.PostRestController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostEditViewModel(private var postData: PostData) {
    private var images: MutableList<Int> = mutableListOf()

    fun changeText(text: String) {
        postData.text_content = text
    }

    fun addImage(imageId: Int) {
        images.add(imageId)
    }

    private fun save() {
        postData.images = images
    }

    fun submit() {
        save()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                PostRestController.createPost(TEST_JWT, postData)
                clear()
            } catch (e: Exception) {
                Log.d("TempTagPostSubmit", e.toString())
            }
        }
    }

    fun clear() {
        images.clear()
        postData.images = images
        postData.text_content = ""
    }
}