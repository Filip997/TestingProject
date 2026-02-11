package com.example.localinformant.splash.presentation.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.localinformant.core.presentation.ScreensNavigator
import com.example.localinformant.databinding.ActivitySplashScreenBinding
import com.example.localinformant.splash.presentation.viewmodels.SplashScreenViewModel
import com.example.localinformant.viewmodels.LoginViewModel
import com.example.localinformant.views.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator

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
            screensNavigator.navigateToMainActivity(true)
        } else {
            screensNavigator.navigateToLanguageActivity()
        }
    }
}