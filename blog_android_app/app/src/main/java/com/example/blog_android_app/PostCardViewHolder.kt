package com.example.blog_android_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class PostCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val viewPager = itemView.findViewById<ViewPager2>(R.id.viewpager)
    val username = itemView.findViewById<TextView>(R.id.username)
    val text = itemView.findViewById<TextView>(R.id.text)
    val likebutton = itemView.findViewById<ImageButton>(R.id.like)

    fun bind(postItem: PostItem) {
        username.text = postItem.username
        text.text = postItem.text
        setLikeButtonIcon(postItem.like, likebutton)

        if (postItem.imageUrls.isNullOrEmpty()) {
            viewPager.visibility = View.GONE
        } else {
            viewPager.adapter = ImagePagerAdapter(downloadBitmaps(postItem.imageUrls))
        }

        likebutton.setOnClickListener {
            postItem.like = !postItem.like
            setLikeButtonIcon(postItem.like, likebutton)
        }
    }

    private fun downloadBitmaps(imageUrls: List<String>): MutableList<Bitmap?> {
        val bitmapsList = mutableListOf<Bitmap?>()
        val futures: MutableList<Future<Bitmap?>> = mutableListOf()
        val executor = Executors.newSingleThreadExecutor()
        for (url in imageUrls) {
            futures.add(executor.submit(Callable<Bitmap?> {
                try {
                    val urlConnection: HttpURLConnection =
                        URL(url).openConnection() as HttpURLConnection
                    urlConnection.connectTimeout = 5000
                    urlConnection.readTimeout = 5000
                    urlConnection.requestMethod = "GET"
                    urlConnection.connect()

                    val inputStream: InputStream = urlConnection.inputStream
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    Log.e(
                        "DownloadError",
                        "Failed to download image from $url: ${e.message}"
                    )
                    null
                }
            }))
        }
        for (future in futures) {
            try {
                val bitmap = future.get()
                bitmapsList.add(bitmap)
            } catch (e: Exception) {
                Log.e("DownloadError", "Error downloading image: ${e.message}")
            }
        }
        executor.shutdown()
        return bitmapsList
    }

    private fun setLikeButtonIcon(like: Boolean, likebutton: ImageButton) {
        if (like) {
            val likeActive = ContextCompat.getDrawable(itemView.context, R.drawable.favorite_active)
            likebutton.setImageDrawable(likeActive)
        } else {
            val likeInactive = ContextCompat.getDrawable(itemView.context, R.drawable.favorite_inactive)
            likebutton.setImageDrawable(likeInactive)
        }
    }
}