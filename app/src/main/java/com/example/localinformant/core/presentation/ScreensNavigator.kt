package com.example.localinformant.core.presentation

import android.app.Activity
import com.example.localinformant.language.presentation.activities.LanguageActivity
import com.example.localinformant.views.activities.LoginActivity
import com.example.localinformant.views.activities.LoginChooserActivity
import com.example.localinformant.views.activities.MainActivity
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ScreensNavigator @Inject constructor(
    private val activity: Activity
) {

    fun navigateToLanguageActivity() {
        LanguageActivity.start(activity)
    }

    fun navigateToMainActivity(shouldFinish: Boolean = false) {
        MainActivity.start(activity, shouldFinish)
    }

    fun navigateToLoginChooserActivity() {
        LoginChooserActivity.start(activity)
    }

    fun navigateToLoginActivity(userType: String) {
        LoginActivity.start(activity, userType)
    }
}