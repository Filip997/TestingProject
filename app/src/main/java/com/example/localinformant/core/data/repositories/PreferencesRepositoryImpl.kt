package com.example.localinformant.core.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.localinformant.constants.SharedPrefKeys
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.di.qualifiers.AppSharedPreferences
import com.example.localinformant.di.qualifiers.UserSharedPreferences
import com.example.localinformant.models.Company
import com.example.localinformant.models.Person
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    @param:AppSharedPreferences
    private val appSharedPreferences: SharedPreferences,
    @param:UserSharedPreferences
    private val userSharedPreferences: SharedPreferences
) : PreferencesRepository {

    override fun saveAppLanguage(languageCode: String) {
        appSharedPreferences.edit {
            putString(SharedPrefKeys.APP_LANGUAGE, languageCode)
        }
    }

    override fun getAppLanguage() = appSharedPreferences.getString(SharedPrefKeys.APP_LANGUAGE, "")

    override fun saveUserType(userType: String) {
        userSharedPreferences.edit {
            putString(SharedPrefKeys.USER_TYPE, userType)
        }
    }

    override fun getUserType() = userSharedPreferences.getString(SharedPrefKeys.USER_TYPE, "")

    override fun savePerson(person: Person) {
        userSharedPreferences.edit {
            putString(SharedPrefKeys.PERSON, Gson().toJson(person)).apply()
        }
    }

    override fun getPerson(): Person? {
        return Gson().fromJson(
            userSharedPreferences.getString(SharedPrefKeys.PERSON, null), TypeToken.get(Person::class.java)
        )
    }

    override fun saveCompany(company: Company) {
        userSharedPreferences.edit {
            putString(SharedPrefKeys.COMPANY, Gson().toJson(company)).apply()
        }
    }

    override fun getCompany(): Company? {
        return Gson().fromJson(
            userSharedPreferences.getString(SharedPrefKeys.COMPANY, null), TypeToken.get(Company::class.java)
        )
    }
}