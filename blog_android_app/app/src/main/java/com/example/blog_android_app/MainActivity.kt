package com.example.blog_android_app

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.model.UserData
import com.example.blog_android_app.repository.users.UserRestController
import com.example.blog_android_app.view.edit.EditPostFragment
import com.example.blog_android_app.view.feed.FeedFragment
import com.example.blog_android_app.view.post.PostFragment
import com.example.blog_android_app.view.profile.LoginFragment
import com.example.blog_android_app.view.profile.ProfileFragment
import com.example.blog_android_app.viewmodel.CommentListViewModel
import com.example.blog_android_app.viewmodel.LoginViewModel
import com.example.blog_android_app.viewmodel.PostEditViewModel
import com.example.blog_android_app.viewmodel.PostListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val navigator = Navigator()
    private lateinit var bottomNavBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavBar = findViewById(R.id.bottom_navigation_view)

        if (!UserRestController.isUserLoggedIn(context = applicationContext)) {
            Log.d("LOGIN", "Not logged in")
            navigator.navigateToLoginFragment()
        } else {
            Log.d("LOGIN", "logged in")
            loadFragment(FeedFragment(navigator = Navigator()))
        }

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

        fun navigateToUserProfile(user: UserData) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment(navigator = navigator, viewModel = PostListViewModel(user = user)))
                .addToBackStack(null)
                .commit()
        }

        fun navigateToLoginFragment() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment(LoginViewModel() ,navigator))
                .commit()
            hideBottomNavBar()
        }

        fun navigateToFeedFragment() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FeedFragment(navigator))
                .commit()
        }

        fun showBottomNavBar() {
            bottomNavBar.visibility = View.VISIBLE
        }
        fun hideBottomNavBar() {
            bottomNavBar.visibility = View.GONE
        }
    }
}