package com.example.localinformant.constants

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.localinformant.R

object NavFunctions {
    fun popUpLeftRightNavigation(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.from_right)
            .setExitAnim(R.anim.to_left)
            .setPopEnterAnim(R.anim.from_left)
            .setPopExitAnim(R.anim.to_right)
            .build()
    }

    fun popUpDefaultNavigation(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
            .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
            .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
            .build()
    }

    fun NavController.isFragmentInBackStack(destinationId: Int) =
        try {
            getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }
}