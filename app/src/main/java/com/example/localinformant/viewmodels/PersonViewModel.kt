package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.models.Person
import com.example.localinformant.repositories.PersonRepository
import kotlinx.coroutines.launch

class PersonViewModel : ViewModel() {

    private val personRepository = PersonRepository()

    private val personsMutable = MutableLiveData<List<Person>>()
    val personsLiveData: LiveData<List<Person>> = personsMutable

    private val personMutable = MutableLiveData<Person?>()
    val personLiveData: LiveData<Person?> = personMutable

    fun searchPersonsByName() {
        viewModelScope.launch {
            val persons = personRepository.searchPersonsByName()
            personsMutable.postValue(persons)
        }
    }

    fun getCurrentPerson() {
        viewModelScope.launch {
            val person = personRepository.getCurrentPerson()
            personMutable.postValue(person)
        }
    }

    fun getPersonById(id: String) {
        viewModelScope.launch {
            val person = personRepository.getPersonById(id)
            personMutable.postValue(person)
        }
    }

    fun updatePersonToken(token: String) {
        viewModelScope.launch {
            personRepository.updatePersonToken(token)
        }
    }
}