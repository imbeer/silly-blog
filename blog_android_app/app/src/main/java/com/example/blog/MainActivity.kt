package com.example.blog

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))


        feedAdapter = FeedAdapter(mutableListOf())
        feedRecyclerView = findViewById(R.id.feedRecyclerView)

        feedRecyclerView.layoutManager = LinearLayoutManager(this)
        feedRecyclerView.adapter = feedAdapter

        loadInitialData()

        feedRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                    loadMoreData()
                }
            }
        })
    }

    fun loadInitialData() {
        val initialItems = mutableListOf<PostItem>()
        for (i in 1..20) {
            initialItems.add(PostItem("username $i", "text content for item: $i", "https://avatars.githubusercontent.com/u/76579340?v=4"))
        }
        feedAdapter.addItems(initialItems)
    }

    fun loadMoreData() {
        isLoading = true
        Handler(Looper.getMainLooper()).postDelayed({
            val newItems = mutableListOf<PostItem>()
            val start = feedAdapter.itemCount + 1
            for (i in start until start + 10) {
                newItems.add(PostItem("username $i", "text content for item: $i", "https://avatars.githubusercontent.com/u/76579340?v=4"))
            }
            feedAdapter.addItems(newItems)

            isLoading = false
        }, 100)
    }
}