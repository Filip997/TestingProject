package com.example.localinformant.core.presentation.navigator

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.localinformant.R
import com.example.localinformant.auth.presentation.activities.ForgotPasswordActivity
import com.example.localinformant.auth.presentation.activities.LoginActivity
import com.example.localinformant.auth.presentation.activities.RegisterActivity
import com.example.localinformant.constants.NavFunctions
import com.example.localinformant.constants.NavFunctions.isFragmentInBackStack
import com.example.localinformant.setup.presentation.activities.LanguageActivity
import com.example.localinformant.setup.presentation.activities.LoginChooserActivity
import com.example.localinformant.core.presentation.activities.MainActivity
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ScreensNavigator @Inject constructor(
    private val activity: Activity
) {

    private var navController: NavController? = null

    fun onAttachNavController(navController: NavController) {
        this.navController = navController
    }

    fun onBackPressed() {
        (activity as AppCompatActivity).onBackPressedDispatcher.onBackPressed()
    }

    fun navigateToLanguageActivity() {
        LanguageActivity.start(activity)
    }

    fun navigateToMainActivity(withFlags: Boolean = false, shouldFinish: Boolean = false) {
        MainActivity.start(activity, withFlags, shouldFinish)
    }

    fun navigateToLoginChooserActivity() {
        LoginChooserActivity.start(activity)
    }

    fun navigateToLoginActivity() {
        LoginActivity.start(activity)
    }

    fun navigateToForgotPasswordActivity() {
        ForgotPasswordActivity.start(activity)
    }

    fun navigateToRegisterActivity() {
        RegisterActivity.start(activity)
    }

    fun navigateToHomeFragment() {
        if (navController?.isFragmentInBackStack(R.id.homeFragment) == true) {
            navController?.popBackStack(R.id.homeFragment, false)
        } else {
            navController?.navigate(
                R.id.homeFragment, null,
                NavFunctions.popUpDefaultNavigation()
            )
        }
    }

    fun navigateToSearchFragment() {
        if (navController?.isFragmentInBackStack(R.id.searchFragment) == true) {
            navController?.popBackStack(R.id.searchFragment, false)
        } else {
            navController?.navigate(
                R.id.searchFragment, null,
                NavFunctions.popUpDefaultNavigation()
            )
        }
    }

    fun navigateToNotificationsFragment() {
        if (navController?.isFragmentInBackStack(R.id.notificationsFragment) == true) {
            navController?.popBackStack(R.id.notificationsFragment, false)
        } else {
            navController?.navigate(
                R.id.notificationsFragment, null,
                NavFunctions.popUpDefaultNavigation()
            )
        }
    }

    fun navigateToMyAccountFragment() {
        if (navController?.isFragmentInBackStack(R.id.myAccountFragment) == true) {
            navController?.popBackStack(R.id.myAccountFragment, false)
        } else {
            navController?.navigate(
                R.id.myAccountFragment, null,
                NavFunctions.popUpDefaultNavigation()
            )
        }
    }
}