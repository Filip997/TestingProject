package com.example.localinformant.views.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.constants.NetworkConstants
import com.example.localinformant.databinding.ActivityLoginBinding
import com.example.localinformant.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginViewModel: LoginViewModel

    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setupViewModels()

        if (intent.hasExtra(IntentKeys.USER_TYPE)) {
            userType = intent.getStringExtra(IntentKeys.USER_TYPE)
            setupUI()
            setupButtonClicks()
        }
    }

    private fun setupUI() {
        if (userType == AppConstants.PERSON) {
            //binding.layoutEmail.visibility = View.VISIBLE
            binding.tvEmail.text = "Email"
            //binding.layoutPassword.visibility = View.VISIBLE

            //binding.layoutCompanyEmail.visibility = View.GONE
        } else if (userType == AppConstants.COMPANY) {
            //binding.layoutEmail.visibility = View.VISIBLE
            binding.tvEmail.text = "Company Email"
            /*binding.layoutPassword.visibility = View.VISIBLE
            binding.layoutCompanyEmail.visibility = View.VISIBLE*/
        }
    }

    private fun setupButtonClicks() {
        val spannableString = SpannableString("Don't have an account? Register")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                intent.putExtra(IntentKeys.USER_TYPE, userType)
                startActivity(intent)
            }
        }

        spannableString.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvRegister.text = spannableString
        binding.tvRegister.movementMethod = LinkMovementMethod.getInstance()

        binding.btnLogin.setOnClickListener {
            if (NetworkConstants.isNetworkAvailable(this)) {
                var email = ""
                var password = ""

                var isFormValid = true

                if (binding.etEmail.text?.trim()?.isEmpty()!!) {
                    isFormValid = false
                    binding.tvInvalidEmail.visibility = View.VISIBLE
                } else {
                    binding.tvInvalidEmail.visibility = View.GONE
                    email = binding.etEmail.text?.trim().toString()
                }

                if (binding.etPassword.text?.trim()?.isEmpty()!!) {
                    isFormValid = false
                    binding.tvInvalidPassword.visibility = View.VISIBLE
                } else {
                    binding.tvInvalidPassword.visibility = View.GONE
                    password = binding.etPassword.text?.trim().toString()
                }

                Log.d("emailPassword", isFormValid.toString())

                if (isFormValid) {
                    loginViewModel.loginUserWithEmailAndPassword(email, password)
                }
            } else {
                showAlertDialog(
                    "Network Error",
                    "There's no internet connection",
                    "Cancel"
                )
            }
        }
    }

    private fun setupViewModels() {
        loginViewModel.loginUserLiveData.observe(this) { result ->
            if (result.isSuccessful) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra(IntentKeys.USER_TYPE, userType)
                startActivity(intent)
                finish()
            } else {
                showAlertDialog(
                    "Login failed",
                    result.message ?: "Couldn't login user",
                    "Cancel"
                )
            }
        }

        loginViewModel.isLoading.observe(this) { result ->
            if (result) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showAlertDialog(title: String, message: String, btnText: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(btnText) { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}