package com.example.blog_android_app.view.post

import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.R
import com.example.blog_android_app.model.CommentData
import com.example.blog_android_app.repository.comments.CommentRestController
import com.example.blog_android_app.viewmodel.CommentListViewModel
import kotlinx.coroutines.runBlocking

class CommentCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textUsernameView = itemView.findViewById<TextView>(R.id.comment_username)
    private val textContentView = itemView.findViewById<TextView>(R.id.comment_text)
    private val clearButton = itemView.findViewById<ImageButton>(R.id.comment_clear_button)
    private val editButton = itemView.findViewById<ImageButton>(R.id.comment_edit_button)
    private val adminButtons = itemView.findViewById<LinearLayout>(R.id.admin_comment_buttons)

    fun bind(commentData: CommentData, viewModel: CommentListViewModel) {
        textUsernameView.text = commentData.username
        textContentView.text = commentData.textContent

        if (commentData.canBeEdited) {
            adminButtons.visibility = View.VISIBLE
            clearButton.visibility = View.VISIBLE
            editButton.visibility = View.VISIBLE
        } else {
            adminButtons.visibility = View.GONE
        }

        clearButton.setOnClickListener {
            runBlocking {
                val res = CommentRestController.deleteComment(commentData = commentData)
                if (res) {
                    Toast.makeText(itemView.context, "Comment deleted!", Toast.LENGTH_SHORT).show()
                    viewModel.update()
                }
            }
        }

        editButton.setOnClickListener {
            viewModel.setCurrentEditComment(commentData)
        }
    }
}