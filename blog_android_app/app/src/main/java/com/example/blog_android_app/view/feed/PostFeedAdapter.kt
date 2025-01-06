package com.example.blog_android_app.view.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.viewmodel.PostListViewModel

class PostFeedAdapter(
    private val viewModel: PostListViewModel,
    val navigator: MainActivity.Navigator,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PostCardViewHolder>()
{
    private val postList = mutableListOf<PostData>()

    init {
        viewModel.posts.observe(lifecycleOwner) { newList ->
            postList.clear()
            postList.addAll(newList)
        }

        viewModel.notifyItemRangeInserted.observe(lifecycleOwner) { range ->
            notifyItemRangeInserted(range.first, range.second)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_card, parent, false)
        return PostCardViewHolder(view, navigator)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: PostCardViewHolder, position: Int) {
        holder.bind(postList[position])
    }
}