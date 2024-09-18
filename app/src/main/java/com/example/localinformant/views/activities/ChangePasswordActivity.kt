package com.example.localinformant.views.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.localinformant.R
import com.example.localinformant.databinding.ActivityChangePasswordBinding
import com.example.localinformant.databinding.ActivityLoginSettingsBinding
import com.example.localinformant.viewmodels.LoginViewModel
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {


    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var loginViewModel: LoginViewModel
    private var oldPasswordFilled = false
    private var newPasswordFilled = false
    private var confirmPasswordFilled = false
    private var confirmPasswordMatch = false
    private var newPasswordFormatCorrect = false
    private var newPasswordNotShort = false
    private var newPasswordNotLong = false
    private var doneButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setupButtonClicks()
        setupViewModels()

        binding.changePasswordDoneBt.isEnabled = oldPasswordFilled && newPasswordFilled &&
                confirmPasswordFilled && confirmPasswordMatch && newPasswordFormatCorrect &&
                newPasswordNotLong && newPasswordNotShort
    }

    private fun setupButtonClicks() {
        binding.changePasswordBackArrowIv.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.changePasswordOldPasswordCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { oldPasswordFilled = it.isNotEmpty() }

            binding.changePasswordDoneBt.isEnabled = oldPasswordFilled && newPasswordFilled &&
                    confirmPasswordFilled && confirmPasswordMatch && newPasswordFormatCorrect &&
                    newPasswordNotLong && newPasswordNotShort
        }

        binding.changePasswordNewPasswordCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let {
                newPasswordFilled = it.isNotEmpty()
                binding.changePasswordNewPasswordCard.apply {
                    val noWhiteSpace = !it.startsWith(" ") && !it.endsWith(" ")
                    val correctFormat = checkPassword(it.toString())

                    if (!noWhiteSpace) {
                        isErrorEnabled = true
                        newPasswordFormatCorrect = false
                        errorIconDrawable = null
                        error = context.getString(R.string.password_no_whitespace)
                        boxStrokeErrorColor
                    } else if (it.toString().length < 8) {
                        isErrorEnabled = true
                        newPasswordNotShort = false
                        errorIconDrawable = null
                        error = context.getString(R.string.password_8_chars)
                        boxStrokeErrorColor
                    } else if (!correctFormat) {
                        isErrorEnabled = true
                        errorIconDrawable = null
                        newPasswordFormatCorrect = false
                        error = context.getString(R.string.strong_password)
                        boxStrokeErrorColor
                    } else if (it.toString().length > 20) {
                        isErrorEnabled = true
                        newPasswordNotLong = false
                        errorIconDrawable = null
                        error = context.getString(R.string.password_20_chars)
                        boxStrokeErrorColor
                    } else {
                        newPasswordFormatCorrect = true
                        newPasswordNotLong = true
                        newPasswordNotShort = true
                        isErrorEnabled = false
                    }
                }
            }

            binding.changePasswordDoneBt.isEnabled = oldPasswordFilled && newPasswordFilled &&
                    confirmPasswordFilled && confirmPasswordMatch && newPasswordFormatCorrect &&
                    newPasswordNotLong && newPasswordNotShort
        }

        binding.changePasswordConfirmPasswordCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let {
                confirmPasswordFilled = it.isNotEmpty()
                binding.changePasswordConfirmPasswordCard.apply {
                    val passwordsMatch =
                        it.toString() == binding.changePasswordNewPasswordCard.editText?.text.toString()
                    if (!passwordsMatch) {
                        isErrorEnabled = true
                        errorIconDrawable = null
                        confirmPasswordMatch = false
                        error = context.getString(R.string.passwords_not_match)
                        boxStrokeErrorColor
                    } else {
                        confirmPasswordMatch = true
                        isErrorEnabled = false
                    }
                }
            }
            binding.changePasswordDoneBt.isEnabled = oldPasswordFilled && newPasswordFilled &&
                    confirmPasswordFilled && confirmPasswordMatch && newPasswordFormatCorrect &&
                    newPasswordNotLong && newPasswordNotShort
        }

        binding.changePasswordDoneBt.setOnClickListener {
            val oldPassword = binding.changePasswordOldPasswordCard.editText?.text.toString()
            val newPassword = binding.changePasswordNewPasswordCard.editText?.text.toString()
            loginViewModel.changePassword(oldPassword, newPassword)
            doneButtonPressed = true
        }
    }

    private fun setupViewModels() {
        loginViewModel.isLoading.observe(this) { loading ->
            if (loading)
                binding.changePasswordProgressbar.visibility = View.VISIBLE
            else
                binding.changePasswordProgressbar.visibility = View.GONE
        }

        loginViewModel.changeSuccessful.observe(this) { changePasswordSuccessful ->
            if (doneButtonPressed) {
                doneButtonPressed = false
                if (changePasswordSuccessful) {
                    Toast.makeText(
                        this,
                        getString(R.string.password_change_successful),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    val message: String =
                        if (loginViewModel.changePasswordMessage.value?.contains(getString(R.string.a_network_error))!!)
                            getString(R.string.no_internet_connection)
                        else if (loginViewModel.changePasswordMessage.value?.contains(getString(R.string.the_password_is_invalid))!!)
                            getString(R.string.wrong_password)
                        else
                            loginViewModel.changePasswordMessage.toString()
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun checkPassword(password: String): Boolean {
        var hasUppercase = false
        var hasLowercase = false
        var hasDigits = false
        val hasSpecialCharacter =
            Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[.,=;+()\"#?!@$%^&'_{}|/<>*~:`-]).{8,}$")
                .matcher(password).find()
        var trueStatements = 0

        for (char in password) {
            when {
                char.isDigit() -> hasDigits = true
                char.isUpperCase() -> hasUppercase = true
                char.isLowerCase() -> hasLowercase = true
            }
        }
        if (hasDigits) trueStatements++
        if (hasUppercase) trueStatements++
        if (hasLowercase) trueStatements++
        if (hasSpecialCharacter) trueStatements++

        return trueStatements > 3
    }

}