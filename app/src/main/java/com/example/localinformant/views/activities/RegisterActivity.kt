package com.example.localinformant.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.constants.NetworkConstants
import com.example.localinformant.databinding.ActivityRegisterBinding
import com.example.localinformant.models.Person
import com.example.localinformant.models.RegisterCompanyRequest
import com.example.localinformant.models.RegisterPersonRequest
import com.example.localinformant.viewmodels.RegisterViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var registerViewModel: RegisterViewModel

    private var userType: String? = null

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
    }

    private fun setupUI() {
        if (userType == AppConstants.PERSON) {
            binding.layoutFirstName.visibility = View.VISIBLE
            binding.tvFirstName.text = "First Name"
            binding.layoutLastName.visibility = View.VISIBLE
            binding.tvLastName.text = "Last Name"
            binding.layoutEmail.visibility = View.VISIBLE
            binding.tvEmail.text = "E-mail"
            binding.layoutPassword.visibility = View.VISIBLE
            binding.layoutConfirmPassword.visibility = View.VISIBLE

            binding.layoutCompanyName.visibility = View.GONE
            binding.layoutCompanyEmail.visibility = View.GONE
        } else if (userType == AppConstants.COMPANY) {
            binding.layoutCompanyName.visibility = View.VISIBLE
            binding.layoutCompanyEmail.visibility = View.VISIBLE
            binding.layoutFirstName.visibility = View.VISIBLE
            binding.tvFirstName.text = "Your First Name"
            binding.layoutLastName.visibility = View.VISIBLE
            binding.tvLastName.text = "Your Last Name"
            binding.layoutEmail.visibility = View.VISIBLE
            binding.tvEmail.text = "Your E-mail"
            binding.layoutPassword.visibility = View.VISIBLE
            binding.layoutConfirmPassword.visibility = View.VISIBLE
        }
    }

    private fun setupButtonClicks() {
        binding.btnRegister.setOnClickListener {
            if (NetworkConstants.isNetworkAvailable(this)) {
                if (userType == AppConstants.PERSON) {
                    var firstName = ""
                    var lastName = ""
                    var email = ""
                    var password = ""

                    var isFormValid = true

                    if (binding.etFirstName.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidFirstName.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidFirstName.visibility = View.GONE
                        firstName = binding.etFirstName.text.toString()
                    }

                    if (binding.etLastName.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidLastName.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidLastName.visibility = View.GONE
                        lastName = binding.etLastName.text.toString()
                    }

                    if (binding.etEmail.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidEmail.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidEmail.visibility = View.GONE
                        email = binding.etEmail.text.toString()
                    }

                    if (binding.etPassword.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidPassword.visibility = View.VISIBLE
                    } else if (binding.etPassword.text?.trim()?.length!! < 6) {
                        isFormValid = false
                        binding.tvInvalidPassword.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidPassword.visibility = View.GONE
                        password = binding.etPassword.text.toString()
                    }

                    if (binding.etConfirmPassword.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidConfirmPassword.visibility = View.VISIBLE
                    } else if (binding.etConfirmPassword.text?.trim().toString() != binding.etPassword.text?.trim().toString()) {
                        isFormValid = false
                        binding.tvInvalidConfirmPassword.visibility = View.VISIBLE
                        //Log.d("emailPassword", binding.etConfirmPassword.text?.trim())
                    } else {
                        binding.tvInvalidConfirmPassword.visibility = View.GONE
                    }

                    Log.d("emailPassword", isFormValid.toString())

                    if (isFormValid) {
                        registerViewModel.registerPersonWithEmailAndPassword(
                            RegisterPersonRequest(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                password = password
                            )
                        )
                    }
                } else if (userType == AppConstants.COMPANY) {
                    var companyName = ""
                    var companyEmail = ""
                    var firstName = ""
                    var lastName = ""
                    var email = ""
                    var password = ""

                    var isFormValid = true

                    if (binding.etCompanyName.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidCompanyName.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidCompanyName.visibility = View.GONE
                        companyName = binding.etCompanyName.text.toString()
                    }

                    if (binding.etCompanyEmail.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidCompanyEmail.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidCompanyEmail.visibility = View.GONE
                        companyEmail = binding.etCompanyEmail.text.toString()
                    }

                    if (binding.etFirstName.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidFirstName.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidFirstName.visibility = View.GONE
                        firstName = binding.etFirstName.text.toString()
                    }

                    if (binding.etLastName.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidLastName.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidLastName.visibility = View.GONE
                        lastName = binding.etLastName.text.toString()
                    }

                    if (binding.etEmail.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidEmail.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidEmail.visibility = View.GONE
                        email = binding.etEmail.text.toString()
                    }

                    if (binding.etPassword.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidPassword.visibility = View.VISIBLE
                    } else if (binding.etPassword.text?.trim()?.length!! < 6) {
                        isFormValid = false
                        binding.tvInvalidPassword.visibility = View.VISIBLE
                    } else {
                        binding.tvInvalidPassword.visibility = View.GONE
                        password = binding.etPassword.text.toString()
                    }

                    if (binding.etConfirmPassword.text?.trim()?.isEmpty()!!) {
                        isFormValid = false
                        binding.tvInvalidConfirmPassword.visibility = View.VISIBLE
                    } else if (binding.etConfirmPassword.text?.trim().toString() != binding.etPassword.text?.trim().toString()) {
                        isFormValid = false
                        binding.tvInvalidConfirmPassword.visibility = View.VISIBLE
                        //Log.d("emailPassword", binding.etConfirmPassword.text?.trim())
                    } else {
                        binding.tvInvalidConfirmPassword.visibility = View.GONE
                    }

                    Log.d("emailPassword", isFormValid.toString())

                    if (isFormValid) {
                        registerViewModel.registerCompanyWithEmailAndPassword(
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
        registerViewModel.registerUserLiveData.observe(this) { result ->
            if (result.isSuccessful) {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.putExtra(IntentKeys.USER_TYPE, userType)
                startActivity(intent)
                finish()
            } else {
                showAlertDialog(
                    "Registration failed",
                    result.message ?: "Couldn't register user",
                    "Cancel"
                )
            }
        }

        registerViewModel.isLoading.observe(this) { result ->
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