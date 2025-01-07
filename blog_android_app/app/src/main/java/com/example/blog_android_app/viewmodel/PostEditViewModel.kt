package com.example.blog_android_app.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.images.ImageRestController
import com.example.blog_android_app.repository.posts.PostRestController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PostEditViewModel(private var postData: PostData) {
    private var images: MutableList<Int> = mutableListOf()
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getPostData() = postData

    fun changeText(text: String) {
        postData.textContent = text
    }

    fun addImage(imageId: Int) {
        images.add(imageId)
    }

    fun addFile(uri: Uri, context: Context, lifecycleScope :LifecycleCoroutineScope) {
        val fileSize = getFileSize(uri, context)
        if (fileSize == null || fileSize >= 1_048_576) {
            Log.d("GalleryFragment", "File not accepted: $fileSize bytes")
            return
        }
        val tempFile = File.createTempFile("temp_image", null)
        tempFile.outputStream().use { outputStream ->
            context.contentResolver?.openInputStream(uri)?.copyTo(outputStream)
        }

        _isLoading.value = true
        lifecycleScope.launch(Dispatchers.Default) {
            val resultId = ImageRestController.uploadImage(tempFile)
            withContext(Dispatchers.Main) {
                resultId?.let {
                    images.add(it)
                }
                _isLoading.value = false
            }
        }
    }

    private fun getFileSize(uri: Uri, context: Context): Long? {
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(android.provider.OpenableColumns.SIZE),
            null,
            null,
            null
        )
        return cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    it.getLong(sizeIndex)
                } else null
            } else null
        }
    }

    fun submit(context: Context, lifecycleScope: LifecycleCoroutineScope) {
        postData.images = images
        _isLoading.value = true
        lifecycleScope.launch {
            try {
                if (postData.postId != null) {
                    PostRestController.editPost(post = postData)
                } else {
                    val createdPost = PostRestController.createPost(post = postData)
                    if (createdPost == null) {
                        Log.e("Created Post", "is null")
                    }
                    if (createdPost != null && images.isNotEmpty()) {
                        Log.d("Created Post", "not null")
                        createdPost.images = images
                        PostRestController.editPost(post = createdPost)
                    }
                }
            } catch (e: Exception) {
                Log.d("TempTagPostSubmit", e.toString())
            }
            withContext(Dispatchers.Main) {
                _isLoading.value = false
            }
        }
    }

    fun clear() {
        images.clear()
        postData.images = images
        postData.textContent = ""
    }
}