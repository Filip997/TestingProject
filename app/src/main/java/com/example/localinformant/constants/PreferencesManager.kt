package com.example.localinformant.constants

import com.example.localinformant.application.AppController
import com.example.localinformant.models.Company
import com.example.localinformant.models.Person
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

    fun putPerson(person: Person) {
        AppController.userSharedPreferences.edit().putString(SharedPrefKeys.PERSON, Gson().toJson(person)).apply()
    }

    fun getPerson(): Person {
        return Gson().fromJson(AppController.userSharedPreferences.getString(SharedPrefKeys.PERSON, ""), TypeToken.get(Person::class.java))
    }

    fun putCompany(company: Company) {
        AppController.userSharedPreferences.edit().putString(SharedPrefKeys.COMPANY, Gson().toJson(company)).apply()
    }

    fun getCompany(): Company {
        return Gson().fromJson(AppController.userSharedPreferences.getString(SharedPrefKeys.COMPANY, ""), TypeToken.get(Company::class.java))
    }
}