package com.example.localinformant.core.domain


interface PreferencesRepository {

    fun saveAppLanguage(languageCode: String)
    fun getAppLanguage(): String?
}