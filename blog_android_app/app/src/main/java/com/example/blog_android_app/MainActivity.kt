package com.example.blog_android_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.view.edit.EditPostFragment
import com.example.blog_android_app.view.feed.FeedFragment
import com.example.blog_android_app.view.post.PostFragment
import com.example.blog_android_app.view.profile.ProfileFragment
import com.example.blog_android_app.viewmodel.CommentListViewModel
import com.example.blog_android_app.viewmodel.PostEditViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val navigator = Navigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(FeedFragment(navigator))
                    true
                }
                R.id.nav_add_post -> {
                    loadFragment(EditPostFragment(PostEditViewModel(PostData()), navigator))
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment(navigator))
                    true
                }
                else -> false
            }
        }

        loadFragment(FeedFragment(navigator = Navigator()))
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    inner class Navigator{
        fun navigateToPostFragment(postData: PostData) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PostFragment(CommentListViewModel(postData), navigator))
                .addToBackStack(null)
                .commit()
        }
        fun navigateToPostEditFragment(postData: PostData) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditPostFragment(PostEditViewModel(postData), navigator))
                .addToBackStack(null)
                .commit()
        }
        fun navigateToSelfProfile() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment(navigator))
                .commit()
        }
    }
}