package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.models.Company
import com.example.localinformant.repositories.CompanyRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CompanyViewModel : ViewModel() {

    private val companyRepository = CompanyRepository()

    private val companiesMutable = MutableLiveData<List<Company>>()
    val companiesLiveData: LiveData<List<Company>> = companiesMutable

    private val companyMutable = MutableLiveData<Company?>()
    val companyLiveData: LiveData<Company?> = companyMutable

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean> = _isSuccessful

    private val _isCompanyFollowed = MutableLiveData<Boolean>()
    val isCompanyFollowed: LiveData<Boolean> = _isCompanyFollowed

    fun findCompaniesByName() {
        viewModelScope.launch {
            val companies = companyRepository.searchCompaniesByName()
            companiesMutable.postValue(companies)
        }
    }

    fun getCurrentCompany() {
        viewModelScope.launch {
            val company = companyRepository.getCurrentCompany()
            companyMutable.postValue(company)
        }
    }

    fun getCompanyById(id: String) {
        viewModelScope.launch {
            val person = companyRepository.getCompanyById(id)
            companyMutable.postValue(person)
        }
    }

    fun updateCompanyToken(token: String) {
        viewModelScope.launch {
            companyRepository.updateCompanyToken(token)
        }
    }

    fun followUnfollowCompany(companyId: String) {
        viewModelScope.launch {
            val success = companyRepository.followUnfollowCompany(companyId)
            _isSuccessful.postValue(success)
        }
    }

    fun isCompanyFollowed(companyId: String) {
        viewModelScope.launch {
            val isFollowed = companyRepository.isCompanyFollowed(companyId)
            _isCompanyFollowed.postValue(isFollowed)
        }
    }
}