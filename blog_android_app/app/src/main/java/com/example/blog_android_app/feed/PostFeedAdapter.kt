package com.example.blog_android_app.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.R
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.PostRepository
import kotlinx.coroutines.runBlocking

class PostFeedAdapter(private val items: MutableList<PostData>) :
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

    private fun addItems(newItems: List<PostData>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    fun loadInitialData() {
        runBlocking {
            val initialItems = PostRepository.fetchPosts(offset = itemCount, limit = 5)
            addItems(initialItems)
        }
    }

    fun loadMoreData() {
        loading = true
        runBlocking {
            val newItems = PostRepository.fetchPosts(offset = itemCount, limit = 5)

            addItems(newItems)
            loading = false
        }
    }
}