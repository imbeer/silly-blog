package com.example.blog_android_app.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.blog_android_app.model.UserData
import com.example.blog_android_app.repository.users.UserRestController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class ProfileEditViewModel(
    var userData: UserData = UserRestController.user
) {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _deleted = MutableLiveData(false)
    val deleted: LiveData<Boolean> get() = _deleted

    private val _imagesChanged = MutableLiveData(false)
    val imagesChanged: LiveData<Boolean> get() = _imagesChanged

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
            UserRestController.uploadImage(tempFile)
            withContext(Dispatchers.Main) {
                _imagesChanged.value = true
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

    fun submit(bioText: String, usernameText: String, password: String, context: Context, lifecycleScope: LifecycleCoroutineScope) {
        userData.username = usernameText
        userData.bio = bioText
        _isLoading.value = true
        lifecycleScope.launch {
            try {
                UserRestController.updateUser(userData)
            } catch (e: Exception) {
                Log.d("TempTagPostSubmit", e.toString())
            }
            withContext(Dispatchers.Main) {
                _isLoading.value = false
            }
        }
    }

    fun delete(context: Context, lifecycleScope: LifecycleCoroutineScope) {
        _isLoading.value = true
        runBlocking {
            try {
                UserRestController.deleteUser(context)
            } catch (e: Exception) {
                Log.d("TempTagPostSubmit", e.toString())
            }
            withContext(Dispatchers.Main) {
                _isLoading.value = false
            }
        }
    }
}