package com.example.blog_android_app.view.edit

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.viewmodel.PostEditViewModel


class EditPostFragment(
    private val viewModel: PostEditViewModel,
    private val navigator: MainActivity.Navigator
) : Fragment() {

    private lateinit var editText: EditText
    private lateinit var submitButton: ImageButton
    private lateinit var attachButton: ImageButton
    private lateinit var clearButton: ImageButton

    private lateinit var imageList: RecyclerView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.d("Gallery", "Selected Image URI: $it")
            context?.let { it1 -> viewModel.addFile(uri, it1, lifecycleScope) }
        }
    }

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

        viewModel.isLoading.observe(viewLifecycleOwner) { imageIsLoading ->
            if (imageIsLoading) {
                submitButton.setOnClickListener {}
                submitButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.check_inactive))
            } else {
                submitButton.setOnClickListener { submit() }
                submitButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.check_active))
            }
        }

        viewModel.deleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show()
                navigator.navigateToSelfProfile()
            }
        }

        viewModel.imagesChanged.observe(viewLifecycleOwner) { imageChanged ->
            if (imageChanged) {
                Toast.makeText(context, "Image added successfully!", Toast.LENGTH_SHORT).show()
            }
        }

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
        viewModel.submit(lifecycleScope = lifecycleScope, context = requireContext())
        navigator.navigateToSelfProfile()
    }
    private fun clear() {
        editText.text.clear()
        viewModel.clear(lifecycleScope = lifecycleScope)
    }
    private fun attach() {
        pickImageLauncher.launch("image/*")
    }
}
