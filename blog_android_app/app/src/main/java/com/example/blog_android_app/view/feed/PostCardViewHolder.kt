package com.example.blog_android_app.view.feed

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.blog_android_app.R
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.likes.LikeRestController

class PostCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val viewPager = itemView.findViewById<ViewPager2>(R.id.image_viewpager)
    private val textUsernameView = itemView.findViewById<TextView>(R.id.username)
    private val textContentView = itemView.findViewById<TextView>(R.id.text_content)
    private val likeButton = itemView.findViewById<ImageButton>(R.id.like)
    private val likeNumber = itemView.findViewById<TextView>(R.id.like_number)
//    val imageview = itemView.findViewById<ImageView>(R.id.imageview)

    fun bind(postData: PostData) {
        textUsernameView.text = postData.username
        textContentView.text = postData.textContent
        setLikeButtonIcon(postData.isLiked, likeButton)
        setLikeNumber(postData)
        viewPager.visibility = View.GONE

//        if (postData.images.isNullOrEmpty()) {
//            viewPager.visibility = View.GONE
//        } else {
//            viewPager.adapter = ImagePagerAdapter(downloadBitmaps(postData.images))
//        }

        likeButton.setOnClickListener {
            LikeRestController.chaneLikeStatus(postData)
            setLikeButtonIcon(postData.isLiked, likeButton)
            setLikeNumber(postData)
        }
    }

    private fun setLikeButtonIcon(like: Boolean, likeButton: ImageButton) {
        if (like) {
            val likeActive = ContextCompat.getDrawable(itemView.context, R.drawable.favorite_active)
            likeButton.setImageDrawable(likeActive)
        } else {
            val likeInactive = ContextCompat.getDrawable(itemView.context,
                R.drawable.favorite_inactive
            )
            likeButton.setImageDrawable(likeInactive)
        }
    }

    private fun setLikeNumber(postData: PostData) {
        likeNumber.text = postData.likes.toString()
    }
}