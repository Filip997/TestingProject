package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.models.LoginUserResponse
import com.example.localinformant.repositories.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val loginRepository = LoginRepository()

    private val loginUserMutable = MutableLiveData<LoginUserResponse>()
    val loginUserLiveData: LiveData<LoginUserResponse> = loginUserMutable

    private val isLoadingMutable = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoadingMutable

    fun loginUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            isLoadingMutable.postValue(true)
            loginRepository.loginUserWithEmailAndPassword(email, password, loginUserMutable)
            isLoadingMutable.postValue(false)
        }
    }
}