package com.example.blog_android_app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PostFeedAdapter(private val items: MutableList<PostItem>) :
    RecyclerView.Adapter<PostCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_card, parent, false)
        return PostCardViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PostCardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(newItems: List<PostItem>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }
}