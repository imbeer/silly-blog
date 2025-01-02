package com.example.blog_android_app.feed

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.R

class PostFeedAdapter(private val items: MutableList<PostItem>) :
    RecyclerView.Adapter<PostCardViewHolder>() {
    private var loading = false
    fun isLoading():Boolean = loading

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_card, parent, false)
        return PostCardViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PostCardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    private fun addItems(newItems: List<PostItem>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    fun loadInitialData() {
        val initialItems = mutableListOf<PostItem>()
        for (i in 1..5) {
            initialItems.add(generatePostItem(i))
        }
        addItems(initialItems)
    }

    fun loadMoreData() {
        loading = true
        Handler(Looper.getMainLooper()).postDelayed({
            val newItems = mutableListOf<PostItem>()
            val start = itemCount + 1
            for (i in start until start + 5) {
                newItems.add(generatePostItem(i))
            }
            addItems(newItems)

            loading = false
        }, 100)
    }

    private fun generatePostItem(index: Int): PostItem {
        return PostItem("username$index",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                listOf("https://avatars.githubusercontent.com/u/76579340", "https://avatars.githubusercontent.com/u/48876018"))
    }

}