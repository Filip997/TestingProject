package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.User
import com.example.localinformant.repositories.CompanyRepository
import com.example.localinformant.repositories.PersonRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val personRepository = PersonRepository()
    private val companyRepository = CompanyRepository()

    private val usersMutable = MutableLiveData<List<User>>()
    val usersLiveData: LiveData<List<User>> = usersMutable

    fun searchUsersByName(nameQuery: String) {
        viewModelScope.launch {
            val persons = personRepository.searchPersonsByName(nameQuery)
            val companies = companyRepository.searchCompaniesByName(nameQuery)

            val users = ArrayList<User>()

            persons.forEach {
                users.add(User(it.id!!, "${it.firstName} ${it.lastName}", AppConstants.PERSON))
            }

            companies.forEach {
                users.add(User(it.id, it.name, AppConstants.COMPANY))
            }

            usersMutable.postValue(users)
        }
    }
}