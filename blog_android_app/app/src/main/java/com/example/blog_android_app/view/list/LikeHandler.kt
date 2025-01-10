package com.example.blog_android_app.view.list

import android.content.Context
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.blog_android_app.R
import com.example.blog_android_app.model.PostData

object LikeHandler {
    fun setLikeButtonIcon(context: Context, like: Boolean, likeButton: ImageButton) {
        if (like) {
            val likeActive = ContextCompat.getDrawable(context, R.drawable.favorite_active)
            likeButton.setImageDrawable(likeActive)
        } else {
            val likeInactive = ContextCompat.getDrawable(context,
                R.drawable.favorite_inactive
            )
            likeButton.setImageDrawable(likeInactive)
        }
    }

    fun setLikeNumber(likeNumber: TextView, postData: PostData) {
        likeNumber.text = postData.likes.toString()
    }
}