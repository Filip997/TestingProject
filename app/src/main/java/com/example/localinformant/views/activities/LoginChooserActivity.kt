package com.example.localinformant.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.databinding.ActivityLoginChooserBinding

class LoginChooserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginChooserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginChooserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtonClicks()
    }

    private fun setupButtonClicks() {
        binding.btnPerson.setOnClickListener {
            goToLoginActivity(AppConstants.PERSON)
        }

        binding.btnCompany.setOnClickListener {
            goToLoginActivity(AppConstants.COMPANY)
        }
    }

    private fun goToLoginActivity(userType: String) {
        val intent = Intent(this@LoginChooserActivity, LoginActivity::class.java)
        intent.putExtra(IntentKeys.USER_TYPE, userType)
        startActivity(intent)
    }
}