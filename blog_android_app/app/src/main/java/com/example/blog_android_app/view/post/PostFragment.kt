package com.example.blog_android_app.view.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.blog_android_app.DEFAULT_POSTS_WHEN_TO_LOAD
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.repository.likes.LikeRestController
import com.example.blog_android_app.repository.users.UserRestController
import com.example.blog_android_app.view.feed.ImagePagerAdapter
import com.example.blog_android_app.view.list.CommentFeedAdapter
import com.example.blog_android_app.view.list.LikeHandler
import com.example.blog_android_app.viewmodel.CommentListViewModel
import kotlinx.coroutines.runBlocking

class PostFragment(
    private val viewModel: CommentListViewModel,
    val navigator: MainActivity.Navigator
) : Fragment() {
    private lateinit var commentList: RecyclerView
    private lateinit var commentFeedAdapter: CommentFeedAdapter
//    private lateinit var searchBar: EditText
//    private lateinit var searchButton: ImageButton
    private lateinit var editButton: ImageButton
    private lateinit var clearButton: ImageButton
    private lateinit var commentBar: EditText
    private lateinit var commentButton: ImageButton

    private lateinit var likeNumber: TextView
    private lateinit var likeButton: ImageButton
    private lateinit var postTextContent: TextView
    private lateinit var postOwnerUsername: TextView
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.post_fragment, container, false)
        commentList = view.findViewById(R.id.comment_list)
        editButton = view.findViewById(R.id.post_edit_button)
        clearButton = view.findViewById(R.id.post_clear_button)
        commentBar = view.findViewById(R.id.comment_bar)
        commentButton = view.findViewById(R.id.comment_submit_button)

        likeNumber = view.findViewById(R.id.like_number)
        likeButton = view.findViewById(R.id.like)
        postTextContent = view.findViewById(R.id.text_content)
        postOwnerUsername = view.findViewById(R.id.username)
        viewPager = view.findViewById(R.id.image_viewpager)

        if (viewModel.getPostData().images.isNullOrEmpty()) {
            viewPager.visibility = View.GONE
        } else {
            viewPager.adapter = ImagePagerAdapter(viewModel.getPostData().images!!)
            viewPager.visibility = View.VISIBLE
        }

        commentList.layoutManager = LinearLayoutManager(context)
        commentFeedAdapter = CommentFeedAdapter(viewModel, navigator, viewLifecycleOwner)
        commentList.adapter = commentFeedAdapter
        viewModel.loadData()

        LikeHandler.setLikeButtonIcon(view.context, viewModel.getPostData().isLiked, likeButton)
        LikeHandler.setLikeNumber(likeNumber, viewModel.getPostData())
        postTextContent.text = viewModel.getPostData().textContent
        postOwnerUsername.text = viewModel.getPostData().username

        hideControlButtons()
        showControlButtons()

        viewModel.currentComment.observe(viewLifecycleOwner) { commentData ->
            if (commentData?.textContent != null) {
                commentBar.setText(commentData.textContent)
            }
        }

        viewModel.notifyDataSetChanged.observe(viewLifecycleOwner) {
            commentList.scrollToPosition(0)
        }

        commentList.addOnScrollListener (object : RecyclerView.OnScrollListener() {
            override fun onScrolled(commentList: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(commentList, dx, dy)
                if (viewModel.isEnded() || viewModel.isLoading()) {
                    return
                }

                val layoutManager = commentList.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - DEFAULT_POSTS_WHEN_TO_LOAD) && firstVisibleItemPosition >= 0) {
                    viewModel.loadData()
                }
            }
        })

        clearButton.setOnClickListener {
            viewModel.deletePost()
            navigator.navigateToSelfProfile()
        }

        commentButton.setOnClickListener {
            if (commentBar.text != null) {
                viewModel.sendComment(text = commentBar.text.toString())
                commentBar.text = null
            }
        }

        likeButton.setOnClickListener {
            LikeRestController.chaneLikeStatus(viewModel.getPostData())
            LikeHandler.setLikeButtonIcon(view.context, viewModel.getPostData().isLiked, likeButton)
            LikeHandler.setLikeNumber(likeNumber, viewModel.getPostData())
        }

        editButton.setOnClickListener {
            navigator.navigateToPostEditFragment(viewModel.getPostData())
        }

        postOwnerUsername.setOnClickListener {
            val userId = viewModel.getPostData().userId
            Log.d("NAVIGATE TO USER PROF", viewModel.getPostData().userId.toString())
            runBlocking {
                val user = UserRestController.getUser(userId!!)
                if (user != null)
                    navigator.navigateToUserProfile(user)
                else {
                    Toast.makeText(context, "User is gone", Toast.LENGTH_SHORT).show()
                }
            }
        }

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
}