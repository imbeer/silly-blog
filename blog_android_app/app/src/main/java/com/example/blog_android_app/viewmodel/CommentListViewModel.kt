package com.example.blog_android_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blog_android_app.DEFAULT_COMMENT_LOAD_LIMIT
import com.example.blog_android_app.model.CommentData
import com.example.blog_android_app.model.PostData
import com.example.blog_android_app.repository.comments.CommentRestController
import com.example.blog_android_app.repository.posts.PostRestController
import kotlinx.coroutines.runBlocking

class CommentListViewModel(
    private val postData: PostData,
    private var username: String = "",
    private val loadLimit: Int = DEFAULT_COMMENT_LOAD_LIMIT
) : ViewModel() {
    private var loading = false
    private var endOfFeed = false
    fun isLoading():Boolean = loading
    fun isEnded(): Boolean = endOfFeed
    fun getUsername(): String = username
    fun getPostData(): PostData = postData

    private val _currentComment = MutableLiveData<CommentData>()
    val currentComment: LiveData<CommentData> get() = _currentComment

    private val _comments = MutableLiveData<MutableList<CommentData>>()
    val comments: LiveData<MutableList<CommentData>> get() = _comments

    private val _notifyItemRangeInserted = MutableLiveData<Pair<Int, Int>>()
    val notifyItemRangeInserted: LiveData<Pair<Int, Int>> get() = _notifyItemRangeInserted

    private val _notifyDataSetChanged = MutableLiveData(false)
    val notifyDataSetChanged: LiveData<Boolean> get() = _notifyDataSetChanged

    init {
        _comments.value = mutableListOf()
    }

    private fun _notifyItemRangeInserted(startIndex: Int, count: Int) {
        _notifyItemRangeInserted.value = Pair(startIndex, count)
    }

    private fun _notifyDataSetChanged() {
        _notifyDataSetChanged.value = true
    }

    private fun addComments(newComments: List<CommentData>) {
        val currentList = _comments.value ?: mutableListOf()
        val startIndex = currentList.size
        currentList.addAll(newComments)
        _comments.value = currentList
        _notifyItemRangeInserted(startIndex, newComments.size)
    }

    fun loadData() {
        loading = true

        runBlocking {
            val newItems = CommentRestController.fetchComments(
                postId = postData.postId!!,
                author = username,
                offset = _comments.value?.size ?: 0,
                limit = loadLimit)
            if (!newItems.isNullOrEmpty()) {
                addComments(newItems)
            } else {
                endOfFeed = true
            }
            loading = false
        }
    }

    fun update() {
        _comments.value?.clear()
        endOfFeed = false
        loadData()
        _notifyDataSetChanged()
    }

    fun search(author: String) {
        username = author
        _comments.value?.clear()
        endOfFeed = false
        loadData()
        _notifyDataSetChanged()
    }

    fun sendComment() {
        if (currentComment.value?.textContent.isNullOrEmpty()) {
            if (currentComment.value?.commentId == null) {
                return
            } else {
                runBlocking {
                    CommentRestController.deleteComment(commentData = currentComment.value!!)
                    _currentComment.value = null
                }
            }
        }
        if (currentComment.value!!.postId != null) {
            runBlocking {
                CommentRestController.editComment(commentData = currentComment.value!!)
            }
        } else {
            _currentComment.value!!.postId = postData.postId
            runBlocking {
                CommentRestController.createComment(commentData = currentComment.value!!)
            }
        }
        _currentComment.value = CommentData(textContent = "")
        update()
    }

    fun deletePost() {
        runBlocking {
            PostRestController.deletePost(post = postData)
        }
    }

    fun setText(text: String) {
        currentComment.value?.textContent = text
    }

    fun setCurrentEditComment(commentData: CommentData) {
        _currentComment.value = commentData
    }
}