package com.example.blog_android_app.view.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.viewmodel.PostEditViewModel

class EditPostFragment(private val viewModel: PostEditViewModel, val navigator: MainActivity.Navigator) : Fragment() {

    private lateinit var editText: EditText
    private lateinit var submitButton: ImageButton
    private lateinit var attachButton: ImageButton
    private lateinit var clearButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_post_fragment, container, false)
        editText = view.findViewById(R.id.edit_text)
        submitButton = view.findViewById(R.id.submit_button)
        attachButton = view.findViewById(R.id.attach_button)
        clearButton = view.findViewById(R.id.post_clear_button)

        editText.setText(viewModel.getPostData().textContent)

        submitButton.setOnClickListener {
            submit()
        }
        attachButton.setOnClickListener {
            attach()
        }
        clearButton.setOnClickListener {
            clear()
        }

        return view
    }


    private fun submit() {
        if (editText.text.isEmpty()) {
            return
        }
        viewModel.changeText(editText.text.toString())
        viewModel.submit()
        navigator.navigateToSelfProfile()
    }
    private fun clear() {
        editText.text.clear()
        viewModel.clear()
    }
    private fun attach() {
        // todo: open gallery activity and choose file, then send it to server.
        viewModel.addImage(1) // todo: change id
    }
}