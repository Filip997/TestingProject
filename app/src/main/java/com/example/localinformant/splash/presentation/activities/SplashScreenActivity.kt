package com.example.localinformant.splash.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.localinformant.databinding.ActivitySplashScreenBinding
import com.example.localinformant.language.presentation.activities.LanguageActivity
import com.example.localinformant.splash.presentation.viewmodels.SplashScreenViewModel
import com.example.localinformant.viewmodels.LoginViewModel
import com.example.localinformant.views.activities.BaseActivity
import com.example.localinformant.views.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(2000L)
            splashScreenViewModel.setAppLanguageOnStart()
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