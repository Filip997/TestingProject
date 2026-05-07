package com.example.localinformant.core.domain.repositories

import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person

interface PreferencesRepository {

    fun saveAppLanguage(languageCode: String)
    fun getAppLanguage(): String?

    fun saveUserType(userType: String)
    fun getUserType(): String?
    fun deleteUserType()

    fun savePerson(person: Person)
    fun getPerson(): Person?
    fun deletePerson()

    fun saveCompany(company: Company)
    fun getCompany(): Company?
    fun deleteCompany()
}