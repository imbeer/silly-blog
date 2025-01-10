package com.example.blog_android_app.view.profile

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
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.viewmodel.ProfileEditViewModel

class EditProfileFragment(
    private val navigator: MainActivity.Navigator,
    private val viewModel: ProfileEditViewModel
) : Fragment() {
    private lateinit var submitButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var pictureButton: ImageButton
    private lateinit var nameEdit: EditText
    private lateinit var bioEdit: EditText

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.d("Gallery", "Selected Image URI: $it")
            context?.let { it1 -> viewModel.addFile(uri, it1, lifecycleScope) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_profile_fragment, container, false)
        submitButton = view.findViewById(R.id.edit_profile_submit_button)
        deleteButton = view.findViewById(R.id.edit_profile_delete_button)
        pictureButton = view.findViewById(R.id.edit_profile_picture_button)
        nameEdit = view.findViewById(R.id.edit_username_text)
        bioEdit = view.findViewById(R.id.edit_bio_text)
        updateView()

        viewModel.imagesChanged.observe(viewLifecycleOwner) { imageChanged ->
            if (imageChanged) {
                Toast.makeText(context, "Image added successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { imageIsLoading ->
            if (imageIsLoading) {
                submitButton.setOnClickListener {}
                submitButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.check_inactive))
            } else {
                submitButton.setOnClickListener { submit() }
                submitButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.check_active))
            }
        }

        submitButton.setOnClickListener {
            submit()
        }

        deleteButton.setOnClickListener {
            viewModel.delete(requireContext(), lifecycleScope)
            navigator.navigateToLoginFragment()
        }

        pictureButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        return view
    }

    fun updateView() {
        nameEdit.setText(viewModel.userData.username)
        bioEdit.setText(viewModel.userData.bio)
    }

    fun submit() {
        viewModel.submit(bioEdit.text.toString(), nameEdit.text.toString(), requireContext(), lifecycleScope)
        updateView()
    }
}