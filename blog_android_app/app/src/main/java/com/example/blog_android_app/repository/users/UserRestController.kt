package com.example.blog_android_app.repository.users

import android.content.Context
import android.util.Log
import com.example.blog_android_app.JSON_TYPE
import com.example.blog_android_app.JWT_KEY
import com.example.blog_android_app.MEDIA_TYPE
import com.example.blog_android_app.PREF_NAME
import com.example.blog_android_app.USERID_KEY
import com.example.blog_android_app.model.UserData
import com.example.blog_android_app.repository.connection.RetrofitInstance
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object UserRestController {
    private val api: UserApiService
    private var _token: String? = null
    val token: String? get() = _token

    private lateinit var _user: UserData
    val user: UserData get() = _user

    init {
        val retrofit = RetrofitInstance.instance
        api = retrofit.create(UserApiService::class.java)
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        _token = prefs.getString(JWT_KEY, null)
        val userId: Int = prefs.getInt(USERID_KEY, -1)
        if (token != null && userId > -1) {
            runBlocking {
                getCurrentUser(userId)
            }
            return true
        } else {
            return false
        }
    }

    private fun setUserToken(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
             .putString(JWT_KEY, _token)
             .putInt(USERID_KEY, user.userId!!)
             .apply()
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(JWT_KEY, null)
            .putInt(USERID_KEY, -1)
            .apply()
    }

    suspend fun login(userdata: UserData, context: Context) {
        val result = api.login(constructJson(userdata))
        if (result.isSuccessful) {
            _token = result.body()!!.token
            _user = result.body()!!.user
            setUserToken(context)
        } else {
            throw RuntimeException("Wrong data")
        }
    }

    suspend fun getCurrentUser(id: Int) {
        val result = api.getUser(id)
        if (result.isSuccessful) {
            _user = result.body()!!
        }
    }

    suspend fun getUser(id: Int): UserData? {
        val result = api.getUser(id)
        return if (result.isSuccessful) {
            result.body()!!
        } else null
    }

    suspend fun register(userdata: UserData, context: Context) {
        val result = api.register(constructJson(userdata))
        if (result.isSuccessful) {
            _token = result.body()!!.token
            _user = result.body()!!.user
            setUserToken(context)
        } else {
            throw RuntimeException("Wrong data")
        }
    }

    suspend fun uploadImage(file: File) {
        try {
            val requestFile = RequestBody.create(
                MEDIA_TYPE,
                file
            )
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val response = api.setImage("Bearer $token", body)
            Log.d("UPLOAD", "Response: $response")
        } catch (e: Exception) {
            Log.e("UPLOAD", "Error: ${e.message}")
            throw e
        }
    }

    suspend fun updateUser(userdata: UserData): Boolean {
        val resp = api.updateUser("Bearer $token",
            constructJson(userdata)
        )
        return resp.isSuccessful
    }

    suspend fun deleteUser() {
        api.deleteUser("Bearer $token")
    }

    private fun constructJson(userdata: UserData): RequestBody = RequestBody.create(
        JSON_TYPE,
        """{"user": {${userdata.username?.let { "\"username\": \"$it\"," } ?: ""}"password": "${userdata.password}","email": "${userdata.email}"}}"""
    )
}