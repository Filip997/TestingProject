package com.example.localinformant.constants

import com.example.localinformant.application.AppController

object PreferencesManager {

    fun putUserToken(token: String) {
        AppController.userSharedPreferences.edit().putString(SharedPrefKeys.USER_TOKEN, token).apply()
    }

    fun getUserToken(): String {
        return AppController.userSharedPreferences.getString(SharedPrefKeys.USER_TOKEN, "") ?: ""
    }

    fun putUserType(userType: String) {
        AppController.userSharedPreferences.edit().putString(SharedPrefKeys.USER_TYPE, userType).apply()
    }

    fun getUserType(): String {
        return AppController.userSharedPreferences.getString(SharedPrefKeys.USER_TYPE, "") ?: ""
    }
}