package com.example.blog_android_app.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.DEFAULT_POSTS_WHEN_TO_LOAD
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.repository.users.UserRestController
import com.example.blog_android_app.view.feed.PostFeedAdapter
import com.example.blog_android_app.viewmodel.PostListViewModel

class ProfileFragment(
    private val navigator: MainActivity.Navigator,
    private val viewModel: PostListViewModel = PostListViewModel(user = UserRestController.user)
) : Fragment() {
    private lateinit var postList: RecyclerView
    private lateinit var adapter: PostFeedAdapter
    private lateinit var username: TextView
    private lateinit var bio: TextView
    private lateinit var image: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        postList = view.findViewById(R.id.profile_recycle_view)
        username = view.findViewById(R.id.profile_username)
        bio = view.findViewById(R.id.profile_bio)
        image = view.findViewById(R.id.profile_user_image)

        postList.layoutManager = LinearLayoutManager(context)
        adapter = PostFeedAdapter(viewModel, navigator, this.viewLifecycleOwner)
        viewModel.loadData()
        postList.adapter = adapter
        username.text = viewModel.user.username
        bio.text = viewModel.user.bio

        viewModel.notifyDataSetChanged.observe(viewLifecycleOwner) {
            postList.scrollToPosition(0)
        }

        postList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        return view
    }
}