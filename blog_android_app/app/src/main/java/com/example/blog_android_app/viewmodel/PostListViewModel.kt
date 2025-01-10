package com.example.blog_android_app.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blog_android_app.DEFAULT_POSTS_LOAD_LIMIT
import com.example.blog_android_app.TIME_SORT
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.model.UserData
import com.example.blog_android_app.repository.posts.PostRestController
import kotlinx.coroutines.runBlocking

class PostListViewModel(
    val user: UserData = UserData(userId = -1),
    private var searchPrompt: String = "",
    private val loadLimit: Int = DEFAULT_POSTS_LOAD_LIMIT
) : ViewModel () {

    private var loading = false
    private var endOfFeed = false

    private val _posts = MutableLiveData<MutableList<PostData>>()
    val posts: LiveData<MutableList<PostData>> get() = _posts

    private val _notifyItemRangeInserted = MutableLiveData<Pair<Int, Int>>()
    val notifyItemRangeInserted: LiveData<Pair<Int, Int>> get() = _notifyItemRangeInserted

    private val _notifyDataSetChanged = MutableLiveData(false)
    val notifyDataSetChanged: LiveData<Boolean> get() = _notifyDataSetChanged

    private fun _notifyItemRangeInserted(startIndex: Int, count: Int) {
        _notifyItemRangeInserted.value = Pair(startIndex, count)
    }

    private fun _notifyDataSetChanged() {
        _notifyDataSetChanged.value = true
    }

    init {
        _posts.value = mutableListOf()
    }

    fun isLoading():Boolean = loading
    fun isEnded(): Boolean = endOfFeed
    fun getUsername(): String? = user.username
    fun getPrompt(): String = searchPrompt
    fun setPrompt(prompt: String = "") {
        searchPrompt = prompt
        update()
    }

    private var sort: Int = TIME_SORT
    fun getSort() = sort
    fun setSort(type: Int = TIME_SORT) {
        if (sort == type) {
            return
        }
        sort = type
        update()
    }

    private fun addPosts(newPosts: List<PostData>) {
        val currentList = _posts.value ?: mutableListOf()
        val startIndex = currentList.size
        currentList.addAll(newPosts)
        _posts.value = currentList
        _notifyItemRangeInserted(startIndex, newPosts.size)
    }


    fun loadData() {
        loading = true

        runBlocking {
            val newItems = PostRestController.fetchPosts(
                sort = sort,
                prompt = searchPrompt,
                author = user.userId!!,
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

    fun update() {
        _posts.value!!.clear()
        endOfFeed = false
        _notifyDataSetChanged()
        loadData()
        _notifyDataSetChanged()
    }
}