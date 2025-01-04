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
    private val viewPager = itemView.findViewById<ViewPager2>(R.id.viewpager)
    private val textUsernameView = itemView.findViewById<TextView>(R.id.username)
    private val textContentView = itemView.findViewById<TextView>(R.id.text)
    private val likeButton = itemView.findViewById<ImageButton>(R.id.like)
//    val imageview = itemView.findViewById<ImageView>(R.id.imageview)

    fun bind(postData: PostData) {
        textUsernameView.text = postData.author
        textContentView.text = postData.text_content
        setLikeButtonIcon(postData.isLiked, likeButton)
        viewPager.visibility = View.GONE

//        if (postData.images.isNullOrEmpty()) {
//            viewPager.visibility = View.GONE
//        } else {
//            viewPager.adapter = ImagePagerAdapter(downloadBitmaps(postData.images))
//        }

        likeButton.setOnClickListener {
            LikeRestController.chaneLikeStatus(postData)
            setLikeButtonIcon(postData.isLiked, likeButton)
        }
    }

    private fun setLikeButtonIcon(like: Boolean, likebutton: ImageButton) {
        if (like) {
            val likeActive = ContextCompat.getDrawable(itemView.context, R.drawable.favorite_active)
            likebutton.setImageDrawable(likeActive)
        } else {
            val likeInactive = ContextCompat.getDrawable(itemView.context,
                R.drawable.favorite_inactive
            )
            likebutton.setImageDrawable(likeInactive)
        }
    }
}