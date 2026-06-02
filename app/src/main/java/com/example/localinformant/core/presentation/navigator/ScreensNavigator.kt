package com.example.localinformant.core.presentation.navigator

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.example.localinformant.R
import com.example.localinformant.auth.presentation.activities.ForgotPasswordActivity
import com.example.localinformant.auth.presentation.activities.LoginActivity
import com.example.localinformant.auth.presentation.activities.RegisterActivity
import com.example.localinformant.conversations.presentation.fragments.NewConversationDialogFragment
import com.example.localinformant.core.presentation.util.NavFunctions
import com.example.localinformant.core.presentation.util.NavFunctions.isFragmentInBackStack
import com.example.localinformant.setup.presentation.activities.LanguageActivity
import com.example.localinformant.setup.presentation.activities.LoginChooserActivity
import com.example.localinformant.main.presentation.activities.MainActivity
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ScreensNavigator @Inject constructor(
    private val activity: Activity
) {

    private var navController: NavController? = null
    private var sideNavController: NavController? = null
    private var primaryNavHost: View? = null
    private var dimOverlay: View? = null
    private var sideSheetNavHost: View? = null

    fun onAttachNavController(navController: NavController) {
        this.navController = navController
    }

    fun onAttachSideNavController(sideNavController: NavController) {
        this.sideNavController = sideNavController
    }

    fun onInitializePrimaryNavHost(primaryNavHost: View) {
        this.primaryNavHost = primaryNavHost
    }

    fun onInitializeDimOverlay(dimOverlay: View) {
        this.dimOverlay = dimOverlay
    }

    fun onInitializeSideSheetNavHost(sideSheetNavHost: View) {
        this.sideSheetNavHost = sideSheetNavHost
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

    fun navigateToHomeFragment(bundle: Bundle? = null) {
        if (navController?.isFragmentInBackStack(R.id.homeFragment) == true && bundle == null) {
            navController?.popBackStack(R.id.homeFragment, false)
        } else {
            navController?.navigate(
                R.id.homeFragment, bundle,
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

    fun navigateToMyAccountFragment(bundle: Bundle? = null) {
        if (navController?.isFragmentInBackStack(R.id.userAccountFragment) == true && bundle == null) {
            navController?.popBackStack(R.id.userAccountFragment, false)
        } else {
            navController?.navigate(
                R.id.userAccountFragment, bundle,
                NavFunctions.popUpDefaultNavigation()
            )
        }
    }

    fun openSettingsSideSheet() {
        primaryNavHost?.visibility = View.GONE
        dimOverlay?.visibility = View.VISIBLE

        sideSheetNavHost?.isVisible = true

        sideSheetNavHost?.post {

            sideSheetNavHost?.translationX = sideSheetNavHost?.width?.toFloat() ?: 0f

            sideSheetNavHost?.animate()
                ?.translationX(0f)
                ?.setDuration(250)
                ?.start()
        }
    }

    fun closeSideSheet() {
        primaryNavHost?.visibility = View.VISIBLE
        dimOverlay?.visibility = View.GONE

        sideSheetNavHost?.animate()
            ?.translationX(sideSheetNavHost?.width?.toFloat() ?: 0f)
            ?.setDuration(250)
            ?.withEndAction {

                sideSheetNavHost?.isVisible = false

                sideNavController?.popBackStack(
                    R.id.settingsSlideFragment,
                    true
                )
            }
            ?.start()
    }

    fun navigateToConversationsFragment() {
        if (navController?.isFragmentInBackStack(R.id.conversationsFragment) == true) {
            navController?.popBackStack(R.id.conversationsFragment, false)
        } else {
            navController?.navigate(
                R.id.conversationsFragment, null,
                NavFunctions.popUpDefaultNavigation()
            )
        }
    }

    fun navigateToChatFragment(bundle: Bundle? = null) {
        if (navController?.isFragmentInBackStack(R.id.chatFragment) == true) {
            navController?.popBackStack(R.id.chatFragment, false)
        } else {
            navController?.navigate(
                R.id.chatFragment, bundle,
                NavFunctions.popUpDefaultNavigation()
            )
        }
    }

    fun openNewConversationDialogFragment() {
        NewConversationDialogFragment().show(
            (activity as AppCompatActivity).supportFragmentManager,
            "NewConversationDialogFragment"
        )
    }
}