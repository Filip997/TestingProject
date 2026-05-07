package com.example.localinformant.account.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.localinformant.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.localinformant.account.presentation.events.ChangePasswordEvent
import com.example.localinformant.account.presentation.events.LogoutUserEvent
import com.example.localinformant.account.presentation.util.toString
import com.example.localinformant.account.presentation.viewmodels.SettingsViewModel
import com.example.localinformant.core.domain.error.ValidationError
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.util.toString
import com.example.localinformant.databinding.FragmentSlideSettingsBinding
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsSlideFragment : Fragment() {

    private var _binding: FragmentSlideSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSlideSettingsBinding.inflate(inflater, container, false)

        setupViewModels()
        setupListeners()

        return binding.root
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.settingsUiState.collect { state ->

                    if (state.isLoading)
                        binding.progressbarSettingsSlide.visibility = View.VISIBLE
                    else
                        binding.progressbarSettingsSlide.visibility = View.GONE

                    binding.btnSettingsSlideChangePassword.isEnabled = state.isChangePasswordEnabled

                    if (state.oldPassword != null) {
                        when (state.oldPasswordError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilSettingsSlideOldPassword.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            else -> {
                                binding.tilSettingsSlideOldPassword.apply {
                                    isErrorEnabled = false
                                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                                }
                            }
                        }
                    }

                    if (state.newPassword != null) {
                        when (state.newPasswordError) {
                            ValidationError.EMPTY_FIELD -> {
                                binding.tilSettingsSlideNewPassword.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.empty_field)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.PASSWORD_TOO_SHORT -> {
                                binding.tilSettingsSlideNewPassword.apply {
                                    isErrorEnabled = true
                                    error = context.getString(R.string.password_too_short_message)
                                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                                    endIconDrawable =
                                        ContextCompat.getDrawable(context, R.drawable.ic_error)
                                    boxStrokeErrorColor
                                }
                            }

                            ValidationError.NO_SPECIAL_CHARACTER -> {
                                binding.tilSettingsSlideNewPassword.apply {
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
                                binding.tilSettingsSlideNewPassword.apply {
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
                                binding.tilSettingsSlideNewPassword.apply {
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
                                binding.tilSettingsSlideNewPassword.apply {
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
                                binding.tilSettingsSlideNewPassword.apply {
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
                settingsViewModel.changePasswordEvent.collect {
                    when (it) {
                        is ChangePasswordEvent.Success -> {
                            CustomInfoDialog(
                                title = getString(R.string.successful),
                                message = getString(R.string.password_changed_successfully)
                            ).show(parentFragmentManager, "info_dialog")
                        }

                        is ChangePasswordEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = it.message.toString(requireContext())
                            ).show(parentFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.logoutEvent.collect {
                    when(it) {
                        is LogoutUserEvent.Success -> {
                            screensNavigator.navigateToLanguageActivity()
                        }

                        is LogoutUserEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = it.message.toString(requireContext())
                            ).show(parentFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.ivSettingsSlideCloseBtn.setOnClickListener {
            binding.layoutSettingsSlideChangePassword.visibility = View.VISIBLE
            binding.layoutSettingsSlideChangePasswordFields.visibility = View.GONE

            binding.tilSettingsSlideOldPassword.editText?.setText("")
            binding.tilSettingsSlideNewPassword.editText?.setText("")

            screensNavigator.closeSideSheet()
        }

        binding.layoutSettingsSlideChangePassword.setOnClickListener {
            binding.layoutSettingsSlideChangePassword.visibility = View.GONE
            binding.layoutSettingsSlideChangePasswordFields.visibility = View.VISIBLE
        }

        binding.tilSettingsSlideOldPassword.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { settingsViewModel.onOldPasswordChange(it.toString()) }
        }

        binding.tilSettingsSlideNewPassword.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { settingsViewModel.onNewPasswordChange(it.toString()) }
        }

        binding.btnSettingsSlideChangePassword.setOnClickListener {
            settingsViewModel.changePassword()
        }

        binding.layoutSettingsSlideLogout.setOnClickListener {
            CustomInfoDialog(
                title = getString(R.string.logout),
                message = getString(R.string.are_you_sure_you_want_to_logout),
                positiveButtonText = getString(R.string.yes),
                positiveButtonClick = {
                    settingsViewModel.logout()
                }
            ).show(parentFragmentManager, "info_dialog")
        }
    }
}