package com.example.localinformant.splash.presentation.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.databinding.ActivitySplashScreenBinding
import com.example.localinformant.splash.presentation.viewmodels.SplashScreenViewModel
import com.example.localinformant.core.presentation.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val splashScreenViewModel: SplashScreenViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            goToHomeScreen()
        }

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(2000L)
            splashScreenViewModel.setAppLanguageOnStart()
            askNotificationPermission()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    goToHomeScreen()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            goToHomeScreen()
        }
    }

    private fun goToHomeScreen() {
        if (splashScreenViewModel.isUserLoggedIn) {
            screensNavigator.navigateToMainActivity(true)
        } else {
            screensNavigator.navigateToLanguageActivity()
        }
    }
}