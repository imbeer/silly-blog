package com.example.blog_android_app.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.blog_android_app.R

class EditPostFragment : Fragment() {

    lateinit var editText: EditText
    lateinit var submitButton: ImageButton
    lateinit var attachButton: ImageButton
    lateinit var clearButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_post_fragment, container, false)
        editText = view.findViewById(R.id.edit_text)
        submitButton = view.findViewById(R.id.submit_button)
        attachButton = view.findViewById(R.id.attach_button)
        clearButton = view.findViewById(R.id.clear_button)

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
        // todo: Send http post request
        editText.text.clear()
    }
    private fun clear() {
        editText.text.clear()
    }
    private fun attach() {
        // todo:
    }
}