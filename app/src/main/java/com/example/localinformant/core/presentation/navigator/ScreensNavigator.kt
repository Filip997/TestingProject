package com.example.localinformant.core.presentation.navigator

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.example.localinformant.auth.presentation.activities.ForgotPasswordActivity
import com.example.localinformant.auth.presentation.activities.LoginActivity
import com.example.localinformant.auth.presentation.activities.RegisterActivity
import com.example.localinformant.setup.presentation.activities.LanguageActivity
import com.example.localinformant.setup.presentation.activities.LoginChooserActivity
import com.example.localinformant.core.presentation.activities.MainActivity
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ScreensNavigator @Inject constructor(
    private val activity: Activity
) {

    fun onBackPressed() {
        (activity as AppCompatActivity).onBackPressedDispatcher.onBackPressed()
    }

    fun navigateToLanguageActivity() {
        LanguageActivity.Companion.start(activity)
    }

    fun navigateToMainActivity(withFlags: Boolean = false, shouldFinish: Boolean = false) {
        MainActivity.Companion.start(activity, withFlags, shouldFinish)
    }

    fun navigateToLoginChooserActivity() {
        LoginChooserActivity.Companion.start(activity)
    }

    fun navigateToLoginActivity() {
        LoginActivity.Companion.start(activity)
    }

    fun navigateToForgotPasswordActivity() {
        ForgotPasswordActivity.Companion.start(activity)
    }

    fun navigateToRegisterActivity() {
        RegisterActivity.Companion.start(activity)
    }
}