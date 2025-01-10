package com.example.blog_android_app

import okhttp3.MediaType

const val BASE_URL = "http://38.180.101.54:8080/"
//const val TEST_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCIsInVzZXIiOiIyMDEifQ.sIZdiqdm9eKzBaElBEbvzUEWTD8HcpZcbppMhWwggxY"
//const val TEST_USERNAME = "TheRealAmogus"
const val PREF_NAME = "SILLY_BLOG_PREFS"
const val JWT_KEY = "JWT"
const val USERID_KEY = "USERID"
const val DEFAULT_POSTS_LOAD_LIMIT = 10
const val DEFAULT_POSTS_WHEN_TO_LOAD = 3
const val DEFAULT_COMMENT_LOAD_LIMIT = 10
val JSON_TYPE = MediaType.parse("application/json; charset=utf-8")
val MEDIA_TYPE = MediaType.parse("image/*")

const val LIKE_SORT = 2
const val TIME_SORT = 1
const val NO_SORT = 0