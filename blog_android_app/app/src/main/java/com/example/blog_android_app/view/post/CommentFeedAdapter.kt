package com.example.blog_android_app.view.post

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.R
import com.example.blog_android_app.model.CommentData
import com.example.blog_android_app.viewmodel.CommentListViewModel

@SuppressLint("NotifyDataSetChanged")
class CommentFeedAdapter(
    private val viewModel: CommentListViewModel,
    lifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<CommentCardViewHolder>() {

    private val commentList = mutableListOf<CommentData>()

    init {
        viewModel.comments.observe(lifecycleOwner) { newList ->
            commentList.clear()
            commentList.addAll(newList)
        }

        viewModel.notifyItemRangeInserted.observe(lifecycleOwner) { range ->
            notifyItemRangeInserted(range.first, range.second)
        }

        // used only when searching (all dataset changed)
        viewModel.notifyDataSetChanged.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.comment_card, parent, false)
        return CommentCardViewHolder(view)
    }

    override fun getItemCount(): Int = commentList.size

    override fun onBindViewHolder(holder: CommentCardViewHolder, position: Int) {
        holder.bind(commentList[position], viewModel)
    }
}