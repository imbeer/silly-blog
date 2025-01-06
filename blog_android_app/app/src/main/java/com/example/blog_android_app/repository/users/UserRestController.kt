package com.example.blog_android_app.repository.users

import android.content.Context
import com.example.blog_android_app.JSON_TYPE
import com.example.blog_android_app.JWT_KEY
import com.example.blog_android_app.PREF_NAME
import com.example.blog_android_app.model.UserData
import com.example.blog_android_app.repository.connection.RetrofitInstance
import okhttp3.RequestBody

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
        return (token != null)
    }

    private fun setUserToken(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
             .putString(JWT_KEY, _token)
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

    private fun constructJson(userdata: UserData): RequestBody = RequestBody.create(
        JSON_TYPE,
        """{"user": {${userdata.username?.let { "\"username\": \"$it\"," } ?: ""}"password": "${userdata.password}","email": "${userdata.email}"}}"""
    )
}