package com.example.localinformant.core.domain.repositories

import com.example.localinformant.models.Company
import com.example.localinformant.models.Person

interface PreferencesRepository {

    fun saveAppLanguage(languageCode: String)
    fun getAppLanguage(): String?

    fun saveUserType(userType: String)
    fun getUserType(): String?

    fun savePerson(person: Person)
    fun getPerson(): Person?

    fun saveCompany(company: Company)
    fun getCompany(): Company?
}