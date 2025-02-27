package com.example.blog_android_app.view.list

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.likes.LikeRestController
import com.example.blog_android_app.repository.users.UserRestController
import com.example.blog_android_app.view.feed.ImagePagerAdapter
import kotlinx.coroutines.runBlocking

class PostCardViewHolder(
    private val itemView: View,
    private val navigator: MainActivity.Navigator
) : RecyclerView.ViewHolder(itemView) {
    private val viewPager = itemView.findViewById<ViewPager2>(R.id.image_viewpager)
    private val textUsernameView = itemView.findViewById<TextView>(R.id.username)
    private val textContentView = itemView.findViewById<TextView>(R.id.text_content)
    private val likeButton = itemView.findViewById<ImageButton>(R.id.like)
    private val likeNumber = itemView.findViewById<TextView>(R.id.like_number)
//    val imageview = itemView.findViewById<ImageView>(R.id.imageview)

    fun bind(postData: PostData) {
        textUsernameView.text = postData.username
        textContentView.text = postData.textContent
        LikeHandler.setLikeButtonIcon(itemView.context, postData.isLiked, likeButton)
        LikeHandler.setLikeNumber(likeNumber, postData)
        viewPager.visibility = View.GONE

        if (postData.images.isNullOrEmpty()) {
            viewPager.visibility = View.GONE
        } else {
            viewPager.adapter = ImagePagerAdapter(postData.images!!)
            viewPager.visibility = View.VISIBLE
        }

        likeButton.setOnClickListener {
            LikeRestController.chaneLikeStatus(postData)
            LikeHandler.setLikeButtonIcon(itemView.context, postData.isLiked, likeButton)
            LikeHandler.setLikeNumber(likeNumber, postData)
        }

        itemView.setOnClickListener {
            navigator.navigateToPostFragment(postData)
        }

        textUsernameView.setOnClickListener {
            val userId = postData.userId
            Log.d("NAVIGATE TO USER PROF", postData.userId.toString())
            runBlocking {
                val user = UserRestController.getUser(userId!!)
                if (user != null)
                    navigator.navigateToUserProfile(user)
                else {
                    Toast.makeText(itemView.context, "User is gone", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}