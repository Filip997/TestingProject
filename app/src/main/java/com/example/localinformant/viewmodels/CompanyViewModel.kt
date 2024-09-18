package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.models.Company
import com.example.localinformant.repositories.CompanyRepository
import kotlinx.coroutines.launch

class CompanyViewModel : ViewModel() {

    private val companyRepository = CompanyRepository()

    private val companiesMutable = MutableLiveData<List<Company>>()
    val companiesLiveData: LiveData<List<Company>> = companiesMutable

    private val companyMutable = MutableLiveData<Company?>()
    val companyLiveData: LiveData<Company?> = companyMutable

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
}