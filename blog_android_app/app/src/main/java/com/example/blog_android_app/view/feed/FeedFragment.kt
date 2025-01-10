package com.example.blog_android_app.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.DEFAULT_POSTS_WHEN_TO_LOAD
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.view.list.PostFeedAdapter
import com.example.blog_android_app.viewmodel.PostListViewModel

class FeedFragment(val navigator: MainActivity.Navigator) : Fragment() {

    private lateinit var postList: RecyclerView
    private lateinit var adapter: PostFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.feed_fragment, container, false)
        postList = view.findViewById(R.id.post_recycler_view)
        postList.layoutManager = LinearLayoutManager(context)
        val viewModel = PostListViewModel()

        adapter = PostFeedAdapter(viewModel, navigator, this.viewLifecycleOwner)
        viewModel.loadData()
        postList.adapter = adapter

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
//                    adapter.notifyItemRangeInserted()
                }
            }
        })
        return view
    }
}