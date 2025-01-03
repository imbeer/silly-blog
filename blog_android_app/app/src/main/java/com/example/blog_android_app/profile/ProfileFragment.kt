package com.example.blog_android_app.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.R
import com.example.blog_android_app.TEST_USERNAME
import com.example.blog_android_app.feed.PostFeedAdapter

class ProfileFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostFeedAdapter
    private lateinit var username: TextView
    private lateinit var bio: TextView
    private lateinit var image: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        recyclerView = view.findViewById(R.id.profile_recycle_view)
        username = view.findViewById(R.id.profile_username)
        bio = view.findViewById(R.id.profile_bio)
        image = view.findViewById(R.id.profile_user_image)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = PostFeedAdapter(mutableListOf(), TEST_USERNAME)
        adapter.loadData()
        recyclerView.adapter = adapter
        username.text = TEST_USERNAME

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!adapter.isLoading() && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                ) {
                    adapter.loadData()
                }
            }
        })
        return view
    }
}