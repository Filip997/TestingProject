package com.example.localinformant.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.localinformant.databinding.ActivitySplashScreenBinding
import com.example.localinformant.viewmodels.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        lifecycleScope.launch {
            delay(2000L)
            goToHomeScreen()
        }
    }


    private fun goToHomeScreen() {
        if (loginViewModel.user != null && loginViewModel.user!!.isEmailVerified) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}