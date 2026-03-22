package com.example.localinformant.core.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.localinformant.constants.SharedPrefKeys
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.data.mappers.toDto
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.di.qualifiers.AppSharedPreferences
import com.example.localinformant.di.qualifiers.UserSharedPreferences
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
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
            putString(SharedPrefKeys.PERSON, Gson().toJson(person.toDto())).apply()
        }
    }

    override fun getPerson(): Person? {
        val personDto = Gson().fromJson(
            userSharedPreferences.getString(SharedPrefKeys.PERSON, null), TypeToken.get(PersonDto::class.java)
        )

        return if (personDto != null) {
            personDto.toDomain()
        } else {
            null
        }
    }

    override fun saveCompany(company: Company) {
        userSharedPreferences.edit {
            putString(SharedPrefKeys.COMPANY, Gson().toJson(company.toDto())).apply()
        }
    }

    override fun getCompany(): Company? {
        val companyDto =  Gson().fromJson(
            userSharedPreferences.getString(SharedPrefKeys.COMPANY, null), TypeToken.get(CompanyDto::class.java)
        )

        return if (companyDto != null) {
            companyDto.toDomain()
        } else {
            null
        }
    }
}