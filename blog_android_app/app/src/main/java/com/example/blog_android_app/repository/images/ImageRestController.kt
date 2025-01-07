package com.example.blog_android_app.repository.images

import android.util.Log
import com.example.blog_android_app.MEDIA_TYPE
import com.example.blog_android_app.repository.connection.RetrofitInstance
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object ImageRestController {
    private val api: ImageApiService

    init {
        val retrofit = RetrofitInstance.instance
        api = retrofit.create(ImageApiService::class.java)
    }

    suspend fun uploadImage(file: File): Int? {
        try {
            val requestFile = RequestBody.create(
                MEDIA_TYPE,
                file
            )
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = api.uploadImage(body)
            Log.d("UPLOAD", "Response: $response")
            return response.body()
        } catch (e: Exception) {
            Log.e("UPLOAD", "Error: ${e.message}")
            throw e
        }
    }
}