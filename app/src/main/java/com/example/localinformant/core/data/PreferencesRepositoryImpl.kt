package com.example.localinformant.core.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.localinformant.constants.SharedPrefKeys
import com.example.localinformant.core.domain.PreferencesRepository
import com.example.localinformant.di.qualifiers.AppSharedPreferences
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    @param:AppSharedPreferences
    private val appSharedPreferences: SharedPreferences
) : PreferencesRepository {

    override fun saveAppLanguage(languageCode: String) {
        appSharedPreferences.edit {
            putString(SharedPrefKeys.APP_LANGUAGE, languageCode)
        }
    }

    override fun getAppLanguage() = appSharedPreferences.getString(SharedPrefKeys.APP_LANGUAGE, "")
}