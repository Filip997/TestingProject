package com.example.localinformant.auth.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.localinformant.R
import com.example.localinformant.auth.domain.error.ValidationError
import com.example.localinformant.auth.presentation.events.RegisterEvent
import com.example.localinformant.auth.presentation.util.toString
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.databinding.ActivityRegisterBinding
import com.example.localinformant.auth.presentation.viewmodels.RegisterViewModel
import com.example.localinformant.core.presentation.activities.BaseActivity
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels()

    @Inject
    lateinit var screensNavigator: ScreensNavigator

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModels()
        setupButtonClicks()
    }

    private fun setupButtonClicks() {

        binding.ivRegisterBackArrow.setOnClickListener {
            screensNavigator.onBackPressed()
        }

        binding.tilRegisterCompanyName.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { registerViewModel.onCompanyNameChange(it.toString()) }
        }

        binding.tilRegisterCompanyEmail.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { registerViewModel.onCompanyEmailChange(it.toString()) }
        }

        binding.tilRegisterFirstName.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { registerViewModel.onFirstNameChange(it.toString()) }
        }

        binding.tilRegisterLastName.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { registerViewModel.onLastNameChange(it.toString()) }
        }

        binding.tilRegisterEmail.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { registerViewModel.onEmailChange(it.toString()) }
        }

        binding.tilRegisterPassword.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { registerViewModel.onPasswordChange(it.toString()) }
        }

        binding.tilRegisterConfirmPassword.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { registerViewModel.onConfirmPasswordChange(it.toString()) }
        }

        binding.cbRegisterAgreement.setOnClickListener {
            val isChecked = binding.cbRegisterAgreement.isChecked
            registerViewModel.onAgreementChecked(isChecked)
        }

        binding.btnRegisterSignUp.setOnClickListener {
            registerViewModel.onRegisterClicked()
        }
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.registerUiState.collect { state ->
                    when(state.userType) {
                        UserType.PERSON -> {
                            val params = binding.tvRegisterFirstNameText.layoutParams as ViewGroup.MarginLayoutParams
                            params.setMargins(0, 0, 0, 0)
                            binding.tvRegisterFirstNameText.layoutParams = params

                            binding.tvRegisterFirstNameText.text = getString(R.string.first_name)
                            binding.tvRegisterLastNameText.text = getString(R.string.last_name)
                            binding.tvRegisterEmailText.text = getString(R.string.email)

                            binding.tvRegisterCompanyEmailText.visibility = View.GONE
                            binding.tilRegisterCompanyName.visibility = View.GONE
                            binding.tvRegisterCompanyEmailText.visibility = View.GONE
                            binding.tilRegisterCompanyEmail.visibility = View.GONE
                        }
                        UserType.COMPANY -> {
                            val params = binding.tvRegisterFirstNameText.layoutParams as ViewGroup.MarginLayoutParams
                            params.setMargins(
                                0,
                                resources.getDimension(R.dimen.margin_padding_size_xxlarge).toInt(),
                                0,
                                0
                            )
                            binding.tvRegisterFirstNameText.layoutParams = params

                            binding.tvRegisterCompanyNameText.visibility = View.VISIBLE
                            binding.tilRegisterCompanyName.visibility = View.VISIBLE
                            binding.tvRegisterCompanyEmailText.visibility = View.VISIBLE
                            binding.tilRegisterCompanyEmail.visibility = View.VISIBLE

                            binding.tvRegisterFirstNameText.text = getString(R.string.your_first_name)
                            binding.tvRegisterLastNameText.text = getString(R.string.your_last_name)
                            binding.tvRegisterEmailText.text = getString(R.string.your_email)
                        }
                        else -> {
                            binding.tvRegisterFirstNameText.text = getString(R.string.first_name)
                            binding.tvRegisterLastNameText.text = getString(R.string.last_name)
                            binding.tvRegisterEmailText.text = getString(R.string.email)

                            binding.tvRegisterCompanyEmailText.visibility = View.GONE
                            binding.tilRegisterCompanyName.visibility = View.GONE
                            binding.tvRegisterCompanyEmailText.visibility = View.GONE
                            binding.tilRegisterCompanyEmail.visibility = View.GONE
                        }
                    }

                    binding.btnRegisterSignUp.isEnabled = state.isRegistrationEnabled

                    if (state.isLoading)
                        binding.progressbarRegister.visibility = View.VISIBLE
                    else
                        binding.progressbarRegister.visibility = View.GONE

                    if (state.companyName != null) {
                        when (state.companyNameError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilRegisterCompanyName.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilRegisterCompanyName.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }

                    if (state.companyEmail != null) {
                        when (state.companyEmailError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilRegisterCompanyEmail.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.INVALID_EMAIL_FORMAT -> {
                                binding.tilRegisterCompanyEmail.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.invalid_email_format)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilRegisterCompanyEmail.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }

                    if (state.firstName != null) {
                        when (state.firstNameError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilRegisterFirstName.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilRegisterFirstName.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }

                    if (state.lastName != null) {
                        when (state.lastNameError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilRegisterLastName.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilRegisterLastName.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }

                    if (state.email != null) {
                        when (state.emailError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilRegisterEmail.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.INVALID_EMAIL_FORMAT -> {
                                binding.tilRegisterEmail.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.invalid_email_format)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilRegisterEmail.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }

                    if (state.password != null) {
                        when (state.passwordError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilRegisterPassword.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.PASSWORD_TOO_SHORT -> {
                                binding.tilRegisterPassword.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.password_too_short_message)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.NO_SPECIAL_CHARACTER -> {
                                binding.tilRegisterPassword.apply {
                                    isErrorEnabled = true
                                    error =
                                        context.getString(R.string.password_should_contain_at_least_one_special_character)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.NO_DIGIT -> {
                                binding.tilRegisterPassword.apply {
                                    isErrorEnabled = true
                                    error =
                                        context.getString(R.string.password_should_contain_at_least_one_digit)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.NO_UPPERCASE_LETTER -> {
                                binding.tilRegisterPassword.apply {
                                    isErrorEnabled = true
                                    error =
                                        context.getString(R.string.password_should_contain_at_least_one_uppercase_letter)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.HAS_WHITESPACE -> {
                                binding.tilRegisterPassword.apply {
                                    isErrorEnabled = true
                                    error =
                                        context.getString(R.string.password_should_not_contain_whitespace)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilRegisterPassword.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }

                    if (state.confirmPassword != null) {
                        when (state.confirmPasswordError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilRegisterConfirmPassword.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.PASSWORDS_DONT_MATCH -> {
                                binding.tilRegisterConfirmPassword.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.passwords_dont_match)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilRegisterConfirmPassword.apply {
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
                registerViewModel.registerEvent.collect { event ->
                    when(event) {
                        is RegisterEvent.NavigateToLogin -> {
                            CustomInfoDialog(
                                title = getString(R.string.registration),
                                message = getString(R.string.registration_successful_message),
                                negativeButtonText = getString(R.string.ok),
                                negativeButtonClick = {
                                    screensNavigator.onBackPressed()
                                }
                            ).show(supportFragmentManager, "info_dialog")
                        }
                        is RegisterEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(this@RegisterActivity)
                            ).show(supportFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }
    }
}