package com.example.blog_android_app.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blog_android_app.DEFAULT_POSTS_LIMIT
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.PostRestController
import kotlinx.coroutines.runBlocking

class PostListViewModel(
    private val username: String = "",
    private val searchPrompt: String = "",
    private val loadLimit: Int = DEFAULT_POSTS_LIMIT
) : ViewModel () {

    private var loading = false
    private var endOfFeed = false
    fun isLoading():Boolean = loading
    fun isEnded(): Boolean = endOfFeed
    fun getUsername(): String = username
    fun getPrompt(): String = searchPrompt

    private val _posts = MutableLiveData<MutableList<PostData>>()
    val posts: LiveData<MutableList<PostData>> get() = _posts

    init {
        _posts.value = mutableListOf()
    }

    private fun addPosts(newPosts: List<PostData>) {
        val currentList = _posts.value ?: mutableListOf()
        val startIndex = currentList.size
        currentList.addAll(newPosts)
        _posts.value = currentList
        _notifyItemRangeInserted(startIndex, newPosts.size)
    }

    private val _notifyItemRangeInserted = MutableLiveData<Pair<Int, Int>>()
    val notifyItemRangeInserted: LiveData<Pair<Int, Int>> get() = _notifyItemRangeInserted

    private fun _notifyItemRangeInserted(startIndex: Int, count: Int) {
        _notifyItemRangeInserted.value = Pair(startIndex, count)
    }

    fun loadData() {
        loading = true

        runBlocking {
            val newItems = PostRestController.fetchPosts(
                author = username,
                offset = _posts.value?.size ?: 0,
                limit = loadLimit)
            if (!newItems.isNullOrEmpty()) {
                addPosts(newItems)
            } else {
                endOfFeed = true
            }
            loading = false
        }
    }
}