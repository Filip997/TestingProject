package com.example.localinformant.views.activities

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
import com.example.localinformant.databinding.ActivityRegisterBinding
import com.example.localinformant.models.RegisterCompanyRequest
import com.example.localinformant.models.RegisterPersonRequest
import com.example.localinformant.viewmodels.RegisterViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var registerViewModel: RegisterViewModel

    private var userType: String? = null
    private var signUpPressed = false
    private var companyNameFilled = false
    private var companyEmailFormatCorrect = false
    private var companyEmailFilled = false
    private var firstNameFilled = false
    private var lastNameFilled = false
    private var emailFormatCorrect = false
    private var emailFilled = false
    private var passwordFormatCorrect = false
    private var passwordFilled = false
    private var passwordNotShort = false
    private var passwordNotLong = false
    private var confirmedPasswordMatch = false
    private var confirmPasswordFilled = false
    private var password = ""
    private var conditionsChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        setupViewModels()

        if (intent.hasExtra(IntentKeys.USER_TYPE)) {
            userType = intent.getStringExtra(IntentKeys.USER_TYPE)
            setupUI()
            setupButtonClicks()
        }
        if (userType == AppConstants.PERSON) {
            companyNameFilled = true
            companyEmailFilled = true
            companyEmailFormatCorrect = true
        }

        binding.signUpContinueEmailBt.isEnabled =
            companyEmailFormatCorrect && companyEmailFilled && companyNameFilled &&
                    firstNameFilled && lastNameFilled && emailFormatCorrect && emailFilled
                    && passwordFilled && passwordFormatCorrect && passwordNotShort
                    && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch
                    && conditionsChecked
    }

    private fun setupUI() {
        if (userType == AppConstants.PERSON) {
            binding.signUpFirstNameTextTv.text = "First Name"
            binding.signUpLastNameTextTv.text = "Last Name"
            binding.signUpEmailTextTv.text = "E-mail"
            binding.signUpCompanyNameTextTv.visibility = View.GONE
            binding.signUpCompanyNameCard.visibility = View.GONE
            binding.signUpCompanyEmailTextTv.visibility = View.GONE
            binding.signUpCompanyEmailCard.visibility = View.GONE
        } else if (userType == AppConstants.COMPANY) {
            binding.signUpCompanyNameTextTv.visibility = View.VISIBLE
            binding.signUpCompanyNameCard.visibility = View.VISIBLE
            binding.signUpCompanyEmailTextTv.visibility = View.VISIBLE

            binding.signUpCompanyEmailCard.visibility = View.VISIBLE
            binding.signUpFirstNameTextTv.text = "Your First Name"
            binding.signUpLastNameTextTv.text = "Your Last Name"
            binding.signUpEmailTextTv.text = "Your E-mail"
        }
    }

    private fun setupButtonClicks() {

        binding.signUpBackArrowIv.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.signUpCompanyNameCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { companyNameFilled = it.isNotEmpty() }
            binding.signUpContinueEmailBt.isEnabled =
                companyEmailFormatCorrect && companyEmailFilled && companyNameFilled &&
                        firstNameFilled && lastNameFilled && emailFormatCorrect && emailFilled
                        && passwordFilled && passwordFormatCorrect && passwordNotShort
                        && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch
                        && conditionsChecked
        }

        binding.signUpCompanyEmailCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { companyEmailFilled = it.isNotEmpty() }
            binding.signUpCompanyEmailCard.apply {
                val correctInput = Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()
                if (!correctInput) {
                    isErrorEnabled = true
                    companyEmailFormatCorrect = false
                    error = context.getString(R.string.email_format_incorrect)
                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                    endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_error)
                    boxStrokeErrorColor
                } else {
                    companyEmailFormatCorrect = true
                    isErrorEnabled = false
                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                }
            }
            binding.signUpContinueEmailBt.isEnabled =
                companyEmailFormatCorrect && companyEmailFilled && companyNameFilled &&
                        firstNameFilled && lastNameFilled && emailFormatCorrect && emailFilled
                        && passwordFilled && passwordFormatCorrect && passwordNotShort
                        && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch
                        && conditionsChecked
        }

        binding.signUpFirstNameCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { firstNameFilled = it.isNotEmpty() }
            binding.signUpContinueEmailBt.isEnabled =
                companyEmailFormatCorrect && companyEmailFilled && companyNameFilled &&
                        firstNameFilled && lastNameFilled && emailFormatCorrect && emailFilled
                        && passwordFilled && passwordFormatCorrect && passwordNotShort
                        && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch
                        && conditionsChecked
        }

        binding.signUpLastNameCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { lastNameFilled = it.isNotEmpty() }
            binding.signUpContinueEmailBt.isEnabled =
                companyEmailFormatCorrect && companyEmailFilled && companyNameFilled &&
                        firstNameFilled && lastNameFilled && emailFormatCorrect && emailFilled
                        && passwordFilled && passwordFormatCorrect && passwordNotShort
                        && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch
                        && conditionsChecked
        }


        binding.signUpEmailCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let { emailFilled = it.isNotEmpty() }
            binding.signUpEmailCard.apply {
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
            binding.signUpContinueEmailBt.isEnabled =
                companyEmailFormatCorrect && companyEmailFilled && companyNameFilled &&
                        firstNameFilled && lastNameFilled && emailFormatCorrect && emailFilled
                        && passwordFilled && passwordFormatCorrect && passwordNotShort
                        && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch
                        && conditionsChecked
        }

        binding.signUpPasswordCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let {
                passwordFilled = it.isNotEmpty()
                password = it.toString()
                binding.signUpPasswordCard.apply {
                    val noWhiteSpace = !it.startsWith(" ") && !it.endsWith(" ")
                    val correctFormat = checkPassword(it.toString())

                    if (!noWhiteSpace) {
                        isErrorEnabled = true
                        passwordFormatCorrect = false
                        errorIconDrawable = null
                        error = context.getString(R.string.password_no_whitespace)
                        boxStrokeErrorColor
                    } else if (password.length < 8) {
                        isErrorEnabled = true
                        passwordNotShort = false
                        errorIconDrawable = null
                        error = context.getString(R.string.password_8_chars)
                        boxStrokeErrorColor
                    } else if (!correctFormat) {
                        isErrorEnabled = true
                        errorIconDrawable = null
                        passwordFormatCorrect = false
                        error = context.getString(R.string.strong_password)
                        boxStrokeErrorColor
                    } else if (password.length > 20) {
                        isErrorEnabled = true
                        passwordNotLong = false
                        errorIconDrawable = null
                        error = context.getString(R.string.password_20_chars)
                        boxStrokeErrorColor
                    } else {
                        passwordFormatCorrect = true
                        passwordNotLong = true
                        passwordNotShort = true
                        isErrorEnabled = false
                    }
                }
                binding.signUpContinueEmailBt.isEnabled =
                    companyEmailFormatCorrect && companyEmailFilled && companyNameFilled &&
                            firstNameFilled && lastNameFilled && emailFormatCorrect && emailFilled
                            && passwordFilled && passwordFormatCorrect && passwordNotShort
                            && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch
                            && conditionsChecked
            }
        }

        binding.signUpConfirmPasswordCard.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let {
                confirmPasswordFilled = it.isNotEmpty()
                binding.signUpConfirmPasswordCard.apply {
                    val passwordsMatch = it.toString() == password
                    if (!passwordsMatch) {
                        isErrorEnabled = true
                        errorIconDrawable = null
                        confirmedPasswordMatch = false
                        error = context.getString(R.string.passwords_not_match)
                        boxStrokeErrorColor
                    } else {
                        confirmedPasswordMatch = true
                        isErrorEnabled = false
                    }
                }
            }
            binding.signUpContinueEmailBt.isEnabled =
                emailFormatCorrect && emailFilled && passwordFilled && passwordFormatCorrect && passwordNotShort
                        && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch && conditionsChecked
        }

        binding.signUpAgreementCb.setOnClickListener {
            conditionsChecked = binding.signUpAgreementCb.isChecked
            binding.signUpContinueEmailBt.isEnabled =
                companyEmailFormatCorrect && companyEmailFilled && companyNameFilled &&
                        firstNameFilled && lastNameFilled && emailFormatCorrect && emailFilled
                        && passwordFilled && passwordFormatCorrect && passwordNotShort
                        && passwordNotLong && confirmPasswordFilled && confirmedPasswordMatch
                        && conditionsChecked
        }


        binding.signUpContinueEmailBt.setOnClickListener {
            signUpPressed = true
            val companyName = binding.signUpCompanyNameCard.editText?.text.toString()
            val companyEmail = binding.signUpCompanyEmailCard.editText?.text.toString()
            val firstName = binding.signUpFirstNameCard.editText?.text.toString()
            val lastName = binding.signUpLastNameCard.editText?.text.toString()
            val email = binding.signUpEmailCard.editText?.text.toString()
            val password = binding.signUpPasswordCard.editText?.text.toString()
            if (userType == AppConstants.PERSON) {
                registerViewModel.registerPersonWithEmail(
                    RegisterPersonRequest(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password
                    )
                )
            } else {
                registerViewModel.registerCompanyWithEmail(
                    RegisterCompanyRequest(
                        companyName,
                        companyEmail,
                        firstName,
                        lastName,
                        email,
                        password
                    )
                )
            }

        }

//        binding.btnRegister.setOnClickListener {
//            if (NetworkConstants.isNetworkAvailable(this)) {
//                if (userType == AppConstants.PERSON) {
//                    var firstName = ""
//                    var lastName = ""
//                    var email = ""
//                    var password = ""
//
//                    var isFormValid = true
//
//                    if (binding.etFirstName.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidFirstName.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidFirstName.visibility = View.GONE
//                        firstName = binding.etFirstName.text.toString()
//                    }
//
//                    if (binding.etLastName.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidLastName.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidLastName.visibility = View.GONE
//                        lastName = binding.etLastName.text.toString()
//                    }
//
//                    if (binding.etEmail.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidEmail.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidEmail.visibility = View.GONE
//                        email = binding.etEmail.text.toString()
//                    }
//
//                    if (binding.etPassword.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidPassword.visibility = View.VISIBLE
//                    } else if (binding.etPassword.text?.trim()?.length!! < 6) {
//                        isFormValid = false
//                        binding.tvInvalidPassword.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidPassword.visibility = View.GONE
//                        password = binding.etPassword.text.toString()
//                    }
//
//                    if (binding.etConfirmPassword.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidConfirmPassword.visibility = View.VISIBLE
//                    } else if (binding.etConfirmPassword.text?.trim()
//                            .toString() != binding.etPassword.text?.trim().toString()
//                    ) {
//                        isFormValid = false
//                        binding.tvInvalidConfirmPassword.visibility = View.VISIBLE
//                        //Log.d("emailPassword", binding.etConfirmPassword.text?.trim())
//                    } else {
//                        binding.tvInvalidConfirmPassword.visibility = View.GONE
//                    }
//
//                    Log.d("emailPassword", isFormValid.toString())
//
//                    if (isFormValid) {
//                        registerViewModel.registerPersonWithEmailAndPassword(
//                            RegisterPersonRequest(
//                                firstName = firstName,
//                                lastName = lastName,
//                                email = email,
//                                password = password
//                            )
//                        )
//                    }
//                } else if (userType == AppConstants.COMPANY) {
//                    var companyName = ""
//                    var companyEmail = ""
//                    var firstName = ""
//                    var lastName = ""
//                    var email = ""
//                    var password = ""
//
//                    var isFormValid = true
//
//                    if (binding.etCompanyName.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidCompanyName.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidCompanyName.visibility = View.GONE
//                        companyName = binding.etCompanyName.text.toString()
//                    }
//
//                    if (binding.etCompanyEmail.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidCompanyEmail.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidCompanyEmail.visibility = View.GONE
//                        companyEmail = binding.etCompanyEmail.text.toString()
//                    }
//
//                    if (binding.etFirstName.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidFirstName.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidFirstName.visibility = View.GONE
//                        firstName = binding.etFirstName.text.toString()
//                    }
//
//                    if (binding.etLastName.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidLastName.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidLastName.visibility = View.GONE
//                        lastName = binding.etLastName.text.toString()
//                    }
//
//                    if (binding.etEmail.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidEmail.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidEmail.visibility = View.GONE
//                        email = binding.etEmail.text.toString()
//                    }
//
//                    if (binding.etPassword.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidPassword.visibility = View.VISIBLE
//                    } else if (binding.etPassword.text?.trim()?.length!! < 6) {
//                        isFormValid = false
//                        binding.tvInvalidPassword.visibility = View.VISIBLE
//                    } else {
//                        binding.tvInvalidPassword.visibility = View.GONE
//                        password = binding.etPassword.text.toString()
//                    }
//
//                    if (binding.etConfirmPassword.text?.trim()?.isEmpty()!!) {
//                        isFormValid = false
//                        binding.tvInvalidConfirmPassword.visibility = View.VISIBLE
//                    } else if (binding.etConfirmPassword.text?.trim()
//                            .toString() != binding.etPassword.text?.trim().toString()
//                    ) {
//                        isFormValid = false
//                        binding.tvInvalidConfirmPassword.visibility = View.VISIBLE
//                        //Log.d("emailPassword", binding.etConfirmPassword.text?.trim())
//                    } else {
//                        binding.tvInvalidConfirmPassword.visibility = View.GONE
//                    }
//
//                    Log.d("emailPassword", isFormValid.toString())
//
//                    if (isFormValid) {
//                        registerViewModel.registerCompanyWithEmailAndPassword(
//                            RegisterCompanyRequest(
//                                companyName,
//                                companyEmail,
//                                firstName,
//                                lastName,
//                                email,
//                                password
//                            )
//                        )
//                    }
//                }
//            } else {
//                showAlertDialog(
//                    "Network Error",
//                    "There's no internet connection",
//                    "Cancel"
//                )
//            }
//        }
    }

    private fun setupViewModels() {


        registerViewModel.isLoading.observe(this) { loading ->
            if (loading)
                binding.signUpProgressbar.visibility = View.VISIBLE
            else
                binding.signUpProgressbar.visibility = View.GONE
        }

        registerViewModel.signupSuccessful.observe(this) { signUpSuccessful ->  // Loading observable for the progress bar
            if (signUpPressed) {
                signUpPressed = false
                if (signUpSuccessful) {
                    Toast.makeText(
                        this, getString(R.string.sign_up_successful), Toast.LENGTH_LONG
                    ).show()
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    val message: String =
                        if (registerViewModel.signupMessage.value?.contains(getString(R.string.the_email_address_is_already_in_use))!!)
                            getString(R.string.email_already_used)
                        else if (registerViewModel.signupMessage.value?.contains(getString(R.string.a_network_error))!!)
                            getString(R.string.no_internet_connection)
                        else
                            registerViewModel.signupMessage.value.toString()
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