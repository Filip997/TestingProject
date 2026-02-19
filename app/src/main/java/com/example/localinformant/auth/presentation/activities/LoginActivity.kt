package com.example.localinformant.auth.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.localinformant.R
import com.example.localinformant.auth.domain.error.ValidationError
import com.example.localinformant.auth.presentation.events.LoginEvent
import com.example.localinformant.auth.presentation.util.toString
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.databinding.ActivityLoginBinding
import com.example.localinformant.auth.presentation.viewmodels.LoginViewModel
import com.example.localinformant.core.presentation.activities.BaseActivity
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModels()
        setupButtonClicks()
    }

    private fun setupButtonClicks() {
        binding.ivLoginBackArrow.setOnClickListener {
            screensNavigator.onBackPressed()
        }

        binding.tilLoginEmail.editText?.doOnTextChanged { text, _, _, _ ->
            text.let { loginViewModel.onEmailChange(it.toString()) }
        }

        binding.tilLoginPassword.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { loginViewModel.onPasswordChange(it.toString()) }
        }

        binding.tvLoginForgotPassword.setOnClickListener {
            screensNavigator.navigateToForgotPasswordActivity()
        }

        binding.tvLoginCreateAccount.setOnClickListener {
            screensNavigator.navigateToRegisterActivity()
        }

        binding.btnLogin.setOnClickListener {
            loginViewModel.onLoginClicked()
        }
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginUiState.collect { state ->
                    when(state.userType) {
                        UserType.PERSON -> binding.tvLoginEmailText.text = getString(R.string.email)
                        UserType.COMPANY -> binding.tvLoginEmailText.text = getString(R.string.company_email)
                        else -> binding.tvLoginEmailText.text = getString(R.string.email)
                    }

                    binding.btnLogin.isEnabled = state.isLoginEnabled

                    if (state.isLoading)
                        binding.progressbarLogin.visibility = View.VISIBLE
                    else
                        binding.progressbarLogin.visibility = View.GONE

                    if (state.email != null) {
                        when (state.emailError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilLoginEmail.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.INVALID_EMAIL_FORMAT -> {
                                binding.tilLoginEmail.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.invalid_email_format)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilLoginEmail.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }

                    if (state.password != null) {
                        when (state.passwordError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilLoginPassword.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilLoginPassword.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginEvent.collect { event ->
                    when (event) {
                        is LoginEvent.NavigateToHome -> {
                            screensNavigator.navigateToMainActivity(true, true)
                        }
                        is LoginEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(this@LoginActivity)
                            ).show(supportFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}