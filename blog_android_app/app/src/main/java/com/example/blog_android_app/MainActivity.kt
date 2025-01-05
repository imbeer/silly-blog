package com.example.blog_android_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.view.profile.ProfileFragment
import com.example.blog_android_app.view.edit.EditPostFragment
import com.example.blog_android_app.view.feed.FeedFragment
import com.example.blog_android_app.viewmodel.PostEditViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(FeedFragment())
                    true
                }
                R.id.nav_add_post -> {
                    loadFragment(EditPostFragment(PostEditViewModel(PostData())))
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        loadFragment(FeedFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}