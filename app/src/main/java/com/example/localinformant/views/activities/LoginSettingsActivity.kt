package com.example.localinformant.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.localinformant.R
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.databinding.ActivityLoginBinding
import com.example.localinformant.databinding.ActivityLoginSettingsBinding
import com.example.localinformant.viewmodels.LoginViewModel
import com.google.android.material.R.style.MaterialAlertDialog_MaterialComponents
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginSettingsBinding
    private lateinit var loginViewModel: LoginViewModel
    private var userType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        val sharedPreferences = getSharedPreferences(
            AppConstants.SHARED_PREFS,
            Context.MODE_PRIVATE
        )
        userType =  sharedPreferences.getString(IntentKeys.USER_TYPE,"").toString()
        setupButtonClicks()
        setupViewModels()
    }


    private fun setupButtonClicks() {
        binding.loginSettingsChangePasswordLayout.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.loginSettingsDeleteAccountLayout.setOnClickListener {
            MaterialAlertDialogBuilder(
                this,
                MaterialAlertDialog_MaterialComponents
            )
                .setMessage(getString(R.string.login_settings_delete_account_confirmation))
                .setNegativeButton(getString(R.string.login_settings_delete_account_no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.login_settings_delete_account_yes)) { _, _ ->
                    loginViewModel.deleteUser(userType)
                    loginViewModel.logout()
                }
                .show()
        }

        binding.loginSettingsBackArrowIv.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun setupViewModels() {
        loginViewModel.signedOut.observe(this) { signedOut -> // Sign out success observable
            loginViewModel.userDeleted.observe(this) { userDeleted ->
                if (userDeleted) {
                    Toast.makeText(
                        this,
                        getString(R.string.login_settings_account_deleted),
                        Toast.LENGTH_SHORT
                    ).show()
                    if (signedOut) {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }


        loginViewModel.isLoading.observe(this) { loading ->
            if (loading)
                binding.loginSettingsProgressbar.visibility = View.VISIBLE
            else
                binding.loginSettingsProgressbar.visibility = View.GONE
        }
    }

}