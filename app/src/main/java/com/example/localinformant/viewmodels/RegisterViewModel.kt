package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.models.RegisterCompanyRequest
import com.example.localinformant.models.RegisterPersonRequest
import com.example.localinformant.models.RegisterUserResponse
import com.example.localinformant.repositories.RegisterRepository
import kotlinx.coroutines.launch
import kotlin.math.truncate

class RegisterViewModel : ViewModel() {

    private val registerRepository = RegisterRepository()

    private val registerUserMutableLiveData = MutableLiveData<RegisterUserResponse>()
    val registerUserLiveData: LiveData<RegisterUserResponse> = registerUserMutableLiveData

    private val isLoadingMutable = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoadingMutable

    fun registerPersonWithEmailAndPassword(request: RegisterPersonRequest) {
        viewModelScope.launch {
            isLoadingMutable.postValue(true)
            registerRepository.registerPersonWithEmailAndPassword(request, registerUserMutableLiveData)
            isLoadingMutable.postValue(false)
        }
    }

    fun registerCompanyWithEmailAndPassword(request: RegisterCompanyRequest) {
        viewModelScope.launch {
            isLoadingMutable.postValue(true)
            registerRepository.registerCompanyWithEmailAndPassword(request, registerUserMutableLiveData)
        }
    }
}