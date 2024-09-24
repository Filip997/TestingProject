package com.example.localinformant.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.User
import com.example.localinformant.repositories.CompanyRepository
import com.example.localinformant.repositories.PersonRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val personRepository = PersonRepository()
    private val companyRepository = CompanyRepository()

    private val usersMutable = MutableLiveData<List<User>>()
    val usersLiveData: LiveData<List<User>> = usersMutable

    private val newTokenMutable = MutableSharedFlow<String>()
    val newTokenFlow: SharedFlow<String> = newTokenMutable

    fun searchUsersByName() {
        viewModelScope.launch {
            //Log.d("searchUserNameQuery", nameQuery)

            val persons = personRepository.searchPersonsByName()
            val companies = companyRepository.searchCompaniesByName()

            Log.d("searchUserPerson", persons.toString() ?: "")
            Log.d("searchUserCompany", companies.toString() ?: "")

            val users = ArrayList<User>()

            persons.forEach {
                users.add(User(it.id, it.fullName, AppConstants.PERSON))
            }

            companies.forEach {
                users.add(User(it.id, it.companyName, AppConstants.COMPANY))
            }

            usersMutable.postValue(users)
        }
    }

    fun setNewToken(token: String) {
        viewModelScope.launch {
            newTokenMutable.emit(token)
        }
    }
}