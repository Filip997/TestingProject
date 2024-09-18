package com.example.localinformant.views.activities

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.localinformant.R
import com.example.localinformant.databinding.ActivityForgotPasswordBinding
import com.example.localinformant.viewmodels.ForgotPasswordViewModel
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    private var resetPressed = false
    private var emailFormatCorrect = false
    private var emailFilled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        forgotPasswordViewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        setupViewModels()
        setupButtonClicks()
        binding.forgotPasswordSendEmailBt.isEnabled = emailFilled && emailFormatCorrect
    }


    private fun setupButtonClicks() {
        binding.forgotPasswordBackArrowIv.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.forgotPasswordEmailCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { emailFilled = it.isNotEmpty() }
            binding.forgotPasswordEmailCard.apply {
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
            binding.forgotPasswordSendEmailBt.isEnabled = emailFilled && emailFormatCorrect
        }

        binding.forgotPasswordSendEmailBt.setOnClickListener {
            val email = binding.forgotPasswordEmailCard.editText?.text.toString()
            forgotPasswordViewModel.resetPassword(email)
            resetPressed = true
        }
    }


    private fun setupViewModels() {

        forgotPasswordViewModel.isLoading.observe(this) { loading ->
            if (loading)
                binding.forgotPasswordProgressbar.visibility = View.VISIBLE
            else
                binding.forgotPasswordProgressbar.visibility = View.GONE
        }

        forgotPasswordViewModel.resetSuccessful.observe(this) { resetSuccessful ->
            if (resetPressed) {
                resetPressed = false
                if (resetSuccessful) {
                    Toast.makeText(this, getString(R.string.email_sent), Toast.LENGTH_SHORT).show()
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    val message: String =
                        if (forgotPasswordViewModel.resetMessage.value?.contains(getString(R.string.there_is_no_user))!!)
                            getString(R.string.no_user_in_database)
                        else if (forgotPasswordViewModel.resetMessage.value?.contains(getString(R.string.a_network_error))!!)
                            getString(R.string.no_internet_connection)
                        else
                            forgotPasswordViewModel.resetMessage.toString()
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}