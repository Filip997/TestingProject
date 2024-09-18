package com.example.localinformant.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.localinformant.R
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.databinding.ActivityLoginBinding
import com.example.localinformant.viewmodels.LoginViewModel
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginViewModel: LoginViewModel

    private var userType: String? = null
    private var emailFormatCorrect = false
    private var loginPressed = false
    private var email = ""
    private var emailFilled = false
    private var passwordFilled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setupViewModels()
        setupUI()
        setupButtonClicks()

        if (intent.hasExtra(IntentKeys.USER_TYPE)) {
            userType = intent.getStringExtra(IntentKeys.USER_TYPE)
        } else {
            val sharedPreferences = getSharedPreferences(
                AppConstants.SHARED_PREFS,
                Context.MODE_PRIVATE
            )
            userType =  sharedPreferences.getString(IntentKeys.USER_TYPE,"").toString()
        }
        binding.logInBt.isEnabled = emailFilled && passwordFilled && emailFormatCorrect
    }

    private fun setupUI() {
        if (userType == AppConstants.PERSON) {
            binding.logInEmailTextTv.text = "Email"
        } else if (userType == AppConstants.COMPANY) {
            binding.logInEmailTextTv.text = "Company Email"
        }
    }

    private fun setupButtonClicks() {
        binding.logInBackArrowIv.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.logInEmailCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { emailFilled = it.isNotEmpty() }
            binding.logInEmailCard.apply {
                val correctInput = Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()
                if (!correctInput) {
                    isErrorEnabled = true
                    emailFormatCorrect = false
                    error = context.getString(R.string.email_format_incorrect)
                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                    endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_error)
                    boxStrokeErrorColor
                } else {
                    emailFormatCorrect = true
                    isErrorEnabled = false
                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                }
            }
            binding.logInBt.isEnabled = emailFilled && passwordFilled && emailFormatCorrect
        }

        binding.logInPasswordCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { passwordFilled = it.isNotEmpty() }
            binding.logInBt.isEnabled = emailFilled && passwordFilled && emailFormatCorrect
        }

        binding.logInForgotPasswordTv.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            intent.putExtra(IntentKeys.USER_TYPE, userType)
            startActivity(intent)
        }

        binding.logInCreateAccountTv.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            intent.putExtra(IntentKeys.USER_TYPE, userType)
            startActivity(intent)
        }

        binding.logInBt.setOnClickListener {
            email = binding.logInEmailCard.editText?.text.toString()
            val password = binding.logInPasswordCard.editText?.text.toString()
            loginViewModel.login(email, password)
            loginPressed = true
        }
    }

    private fun setupViewModels() {
        loginViewModel.isLoading.observe(this) { loading -> // Loading observable for the progress bar
            if (loading)
                binding.logInProgressbar.visibility = View.VISIBLE
            else
                binding.logInProgressbar.visibility = View.GONE
        }

        loginViewModel.loginSuccessful.observe(this) { loginSuccessful -> // Successful login observable
            if (loginPressed) {
                loginPressed = false
                if (loginSuccessful) {
                    val sharedPreferences = getSharedPreferences(
                        AppConstants.SHARED_PREFS,
                        Context.MODE_PRIVATE
                    )
                    sharedPreferences.edit()
                        .putString(IntentKeys.USER_TYPE,userType)
                        .apply()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra(IntentKeys.USER_TYPE, userType)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    val message: String = if (loginViewModel.loginMessage.value?.contains(getString(R.string.there_is_no_user))!!)
                        getString(R.string.no_user_in_database)
                    else if (loginViewModel.loginMessage.value?.contains(getString(R.string.a_network_error))!!)
                        getString(R.string.no_internet_connection)
                    else if (loginViewModel.loginMessage.value?.contains(getString(R.string.the_password_is_invalid))!!)
                        getString(R.string.wrong_password)
                    else
                        loginViewModel.loginMessage.value.toString()
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}