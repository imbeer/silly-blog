package com.example.blog

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class FeedAdapter(private val items: MutableList<PostItem>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView.findViewById<TextView>(R.id.username)
        val text_content = itemView.findViewById<TextView>(R.id.text_content)
        val image_view = itemView.findViewById<ImageView>(R.id.image_view)

        fun bind(postItem: PostItem) {
            username.text = postItem.username
            text_content.text = postItem.text_content
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap? = null

            executor.execute {
                try {
                    val `in` = java.net.URL(postItem.imageUrl).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                    handler.post {
                        image_view.setImageBitmap(image)
                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            image_view.setOnClickListener { view ->
                val intent = Intent(view!!.context, PostActivity::class.java)
                intent.putExtra("username", postItem.username)
                intent.putExtra("text_content", postItem.text_content)
                intent.putExtra("bitmap_url", postItem.imageUrl)
                view.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapter.FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_item, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedAdapter.FeedViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItems(newItems: List<PostItem>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }
}