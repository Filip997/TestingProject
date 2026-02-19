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
import com.example.localinformant.auth.presentation.events.ForgotPasswordEvent
import com.example.localinformant.auth.presentation.util.toString
import com.example.localinformant.auth.presentation.viewmodels.ForgotPasswordViewModel
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.databinding.ActivityForgotPasswordBinding
import com.example.localinformant.core.presentation.activities.BaseActivity
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    @Inject
    lateinit var screensNavigator: ScreensNavigator

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModels()
        setupButtonClicks()
    }

    private fun setupButtonClicks() {
        binding.ivForgotPasswordBackArrow.setOnClickListener {
            screensNavigator.onBackPressed()
        }

        binding.tilForgotPasswordEmail.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { forgotPasswordViewModel.onEmailChange(it.toString()) }
        }

        binding.btnForgotPasswordSendEmail.setOnClickListener {
            forgotPasswordViewModel.resetPassword()
        }
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                forgotPasswordViewModel.forgotPasswordUiState.collect { state ->
                    binding.btnForgotPasswordSendEmail.isEnabled = state.isSendEmailEnabled

                    if (state.isLoading)
                        binding.progressbarForgotPassword.visibility = View.VISIBLE
                    else
                        binding.progressbarForgotPassword.visibility = View.GONE

                    if (state.email != null) {
                        when (state.emailError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilForgotPasswordEmail.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.INVALID_EMAIL_FORMAT -> {
                                binding.tilForgotPasswordEmail.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.invalid_email_format)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilForgotPasswordEmail.apply {
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
                forgotPasswordViewModel.forgotPasswordEvent.collect { event ->
                    when(event) {
                        is ForgotPasswordEvent.ShowSuccessDialog -> {
                            CustomInfoDialog(
                                title = getString(R.string.successful),
                                message = getString(R.string.check_your_email)
                            ).show(supportFragmentManager, "info_dialog")
                        }
                        is ForgotPasswordEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(this@ForgotPasswordActivity)
                            ).show(supportFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ForgotPasswordActivity::class.java)
            context.startActivity(intent)
        }
    }
}