package com.example.blog_android_app.view.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.DEFAULT_POSTS_WHEN_TO_LOAD
import com.example.blog_android_app.LIKE_SORT
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.TIME_SORT
import com.example.blog_android_app.view.list.PostFeedAdapter
import com.example.blog_android_app.viewmodel.PostListViewModel

class SearchFragment(val navigator: MainActivity.Navigator) : Fragment() {

    private lateinit var postList: RecyclerView
    private lateinit var adapter: PostFeedAdapter
    private lateinit var viewModel: PostListViewModel
    private lateinit var searchBar: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var sortButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)
        postList = view.findViewById(R.id.post_recycler_view)
        searchBar = view.findViewById(R.id.search_bar)
        searchButton = view.findViewById(R.id.search_button)
        sortButton = view.findViewById(R.id.sort_button)

        postList.layoutManager = LinearLayoutManager(context)
        val viewModel = PostListViewModel()

        adapter = PostFeedAdapter(viewModel, navigator, this.viewLifecycleOwner)
//        viewModel.loadData()
        postList.adapter = adapter

        viewModel.notifyDataSetChanged.observe(viewLifecycleOwner) {
            postList.scrollToPosition(0)
        }

        viewModel.notifyItemRangeInserted.observe(viewLifecycleOwner) { range->
            adapter.notifyItemRangeInserted(range.first, range.second)
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

        searchButton.setOnClickListener {
            if (searchBar.text != null) viewModel.setPrompt(searchBar.text.toString())
            else viewModel.setPrompt()
        }

        sortButton.setOnClickListener {
            Log.d("change sort", "clicked")
            if (viewModel.getSort() == TIME_SORT) {
                viewModel.setSort(LIKE_SORT)
                sortButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.favorite_inactive))
            } else {
                viewModel.setSort(TIME_SORT)
                sortButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.clock))
            }
        }

        return view
    }
}