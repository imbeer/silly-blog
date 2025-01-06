package com.example.blog_android_app.viewmodel

import android.content.Context
import android.widget.Toast
import com.example.blog_android_app.model.UserData
import com.example.blog_android_app.repository.users.UserRestController
import kotlinx.coroutines.runBlocking

class LoginViewModel {
    lateinit var userData: UserData

    fun register(password: String, email: String, username: String, context: Context): Boolean {
        userData = UserData(password = password, email = email, username = username)
        try {
            runBlocking {
                UserRestController.register(userData, context)
            }
            return true
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun login(password: String, email: String, context: Context): Boolean {
        userData = UserData(password = password, email = email)
        try {
            runBlocking {
                UserRestController.login(userData, context)
            }
            return true
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            return false
        }
    }

}