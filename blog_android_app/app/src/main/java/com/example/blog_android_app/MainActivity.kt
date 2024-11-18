package com.example.blog_android_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blog_android_app.feed.FeedFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, FeedFragment())
            .commit()
    }
}