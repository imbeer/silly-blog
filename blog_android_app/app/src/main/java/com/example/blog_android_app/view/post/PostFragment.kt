package com.example.blog_android_app.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.DEFAULT_POSTS_WHEN_TO_LOAD
import com.example.blog_android_app.R
import com.example.blog_android_app.view.feed.PostCardViewHolder
import com.example.blog_android_app.viewmodel.CommentListViewModel

class PostFragment(
    private val viewModel: CommentListViewModel
) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentFeedAdapter
    private lateinit var postCard: View
    private lateinit var searchBar: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var editButton: ImageButton
    private lateinit var clearButton: ImageButton
    private lateinit var commentBar: EditText
    private lateinit var commentButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.post_fragment, container, false)
        recyclerView = view.findViewById(R.id.comment_list)

        postCard = layoutInflater.inflate(R.layout.post_card, view.findViewById(R.id.post_card), false)
        val viewHolder = PostCardViewHolder(postCard)
        viewHolder.bind(viewModel.getPostData())

        searchBar = view.findViewById(R.id.comment_search_bar)
        searchButton = view.findViewById(R.id.comment_search_button)
        editButton = view.findViewById(R.id.post_edit_button)
        clearButton = view.findViewById(R.id.post_clear_button)
        commentBar = view.findViewById(R.id.comment_bar)
        commentButton = view.findViewById(R.id.comment_submit_button)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CommentFeedAdapter(viewModel, this.viewLifecycleOwner)
        viewModel.loadData()
        recyclerView.adapter = adapter

        hideControlButtons()
        showControlButtons()

        recyclerView.addOnScrollListener (object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (viewModel.isEnded() || viewModel.isLoading()) {
                    return
                }

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - DEFAULT_POSTS_WHEN_TO_LOAD) && firstVisibleItemPosition >= 0) {
                    viewModel.loadData()
                }
            }
        })

        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideControlButtons()
                hideCommentPanel()
            } else {
                showControlButtons()
                showCommentPanel()
            }
        }

        clearButton.setOnClickListener {
            viewModel.deletePost()
        }

        searchButton.setOnClickListener {
            viewModel.search(searchBar.text.toString())
        }

        commentButton.setOnClickListener {
            viewModel.createComment(text = commentBar.text.toString())
        }

        // todo: edit post button.

        return view
    }

    private fun hideControlButtons() {
        editButton.visibility = View.GONE
        clearButton.visibility = View.GONE
    }

    private fun showControlButtons() {
        if (viewModel.getPostData().canBeEdited) {
            editButton.visibility = View.VISIBLE
            clearButton.visibility = View.VISIBLE
        }
    }

    private fun hideCommentPanel() {
        commentButton.visibility = View.GONE
        commentBar.visibility = View.GONE
    }

    private fun showCommentPanel() {
        commentButton.visibility = View.VISIBLE
        commentBar.visibility = View.VISIBLE
    }
}