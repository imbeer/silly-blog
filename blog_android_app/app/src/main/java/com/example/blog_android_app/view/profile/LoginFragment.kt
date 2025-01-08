package com.example.blog_android_app.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.blog_android_app.MainActivity
import com.example.blog_android_app.R
import com.example.blog_android_app.viewmodel.LoginViewModel

class LoginFragment(
    private val viewModel: LoginViewModel,
    val navigator: MainActivity.Navigator
): Fragment() {

    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var emailText: EditText
    private lateinit var loginButton: AppCompatButton
    private lateinit var registerButton: AppCompatButton

    enum class FragmentState{
        LOGIN, REGISTER
    }

    private var state = FragmentState.LOGIN

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        usernameText = view.findViewById(R.id.username_text)
        passwordText = view.findViewById(R.id.password_text)
        emailText = view.findViewById(R.id.email_text)
        loginButton = view.findViewById(R.id.login_button)
        registerButton = view.findViewById(R.id.register_button)

        usernameText.visibility = View.GONE

        registerButton.setOnClickListener {
            if (state == FragmentState.LOGIN) {
                state = FragmentState.REGISTER
                usernameText.visibility = View.VISIBLE
                registerButton.text = "BACK"
                loginButton.text = "SEND"
            } else {
                state = FragmentState.LOGIN
                usernameText.visibility = View.GONE
                registerButton.text = "REGISTER"
                loginButton.text = "LOGIN"
                // todo: clear username from viewmodel
            }
        }

        loginButton.setOnClickListener {
            if (state == FragmentState.LOGIN) {
                if (emailText.text.isNullOrEmpty() || passwordText.text.isNullOrEmpty()) {
                    Toast.makeText(context, "Not enough data", Toast.LENGTH_SHORT).show()
                } else  {
                    val res = viewModel.login(
                        password = passwordText.text.toString(),
                        email = emailText.text.toString(),
                        context = requireContext()
                    )
                    if (res) {
                        navigator.navigateToFeedFragment()
                        navigator.showBottomNavBar()
                    }
                }
            } else {
                if (usernameText.text.isNullOrEmpty() || emailText.text.isNullOrEmpty() || passwordText.text.isNullOrEmpty()) {
                    Toast.makeText(context, "Not enough data", Toast.LENGTH_SHORT).show()
                } else  {
                    val res = viewModel.register(
                        password = passwordText.text.toString(),
                        email = emailText.text.toString(),
                        username = usernameText.text.toString(),
                        context = requireContext()
                    )
                    if (res) {
                        navigator.navigateToSelfProfile()
                        navigator.showBottomNavBar()
                    }
                }
            }
        }

        return view
    }
}