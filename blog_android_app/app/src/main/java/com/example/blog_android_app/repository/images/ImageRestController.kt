package com.example.blog_android_app.repository.images

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.blog_android_app.BASE_URL
import com.example.blog_android_app.MEDIA_TYPE
import com.example.blog_android_app.R
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

    fun downloadImage(imageView: ImageView, imageId: Int) {
        val url = BASE_URL + "image?image_id=$imageId"
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.placeholder_load)
            .error(R.drawable.placeholder_error)
            .into(imageView)
    }

    fun downloadImageByLink(imageView: ImageView, imageLink: String) {
        val url = BASE_URL + "image/" + imageLink
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .into(imageView)
    }
}