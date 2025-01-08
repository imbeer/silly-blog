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

    private val _deleted = MutableLiveData(false)
    val deleted: LiveData<Boolean> get() = _deleted

    private val _imagesChanged = MutableLiveData(false)
    val imagesChanged: LiveData<Boolean> get() = _imagesChanged

    fun getPostData() = postData

    fun changeText(text: String) {
        postData.textContent = text
    }

    fun addImage(imageId: Int) {
        images.add(imageId)
        _imagesChanged.value = true
    }

    fun addFile(uri: Uri, context: Context, lifecycleScope: LifecycleCoroutineScope) {
        val fileSize = getFileSize(uri, context)
        if (fileSize == null || fileSize >= 1_048_576) {
            Log.d("GalleryFragment", "File not accepted: $fileSize bytes")
            _imagesChanged.value = false
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
                if (resultId != null) {
                    images.add(resultId)
                    _imagesChanged.value = true
                } else {
                    _imagesChanged.value = false
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
                    if (createdPost != null && images.isNotEmpty()) {
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

    fun clear(lifecycleScope: LifecycleCoroutineScope) {
        if (postData.postId != null){
            lifecycleScope.launch {
                if (PostRestController.deletePost(post = postData))
                    withContext(Dispatchers.Main) {
                        _deleted.value = true
                    }
                else {
                    withContext(Dispatchers.Main) {
                        _deleted.value = false
                    }
                }
            }
        }
        images.clear()
        _imagesChanged.value = true
        postData.images = images
        postData.textContent = ""
    }
}