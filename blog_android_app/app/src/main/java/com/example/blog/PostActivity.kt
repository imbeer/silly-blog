package com.example.blog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post)

        val usernameTextView = findViewById<TextView>(R.id.username)
        val textContentTextView = findViewById<TextView>(R.id.text_content)
        val imageView = findViewById<ImageView>(R.id.image_view)

        val username = intent.getStringExtra("username")
        val textContent = intent.getStringExtra("text_content")
        val bitmapUtl = intent.getStringExtra("bitmap_url")
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null

        executor.execute {
            try {
                val `in` = java.net.URL(bitmapUtl).openStream()
                image = BitmapFactory.decodeStream(`in`)
                handler.post {
                    imageView.setImageBitmap(image)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }

        usernameTextView.text = username
        textContentTextView.text = textContent
    }
}