package com.example.localinformant.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.repositories.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAccountViewModel : ViewModel() {

    private val loginRepository = FirebaseAuthRepository()

    val user: FirebaseUser? by lazy { loginRepository.currentUser() }
    private val _signedOut = MutableLiveData(false)
    val signedOut = _signedOut

    fun logout() {
        viewModelScope.launch (Dispatchers.IO){
            loginRepository.logout()
            _signedOut.postValue(true)
        }
    }


}