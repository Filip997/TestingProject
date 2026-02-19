package com.example.localinformant.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.auth.data.repositories.FirebaseAuthRepositoryImpl
import com.example.localinformant.auth.domain.repositories.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAccountViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepositoryImpl
) : ViewModel() {

    val user: FirebaseUser? by lazy { authRepository.currentUser() }
    private val _signedOut = MutableLiveData(false)
    val signedOut = _signedOut

    fun logout() {
        viewModelScope.launch (Dispatchers.IO){
            authRepository.logout()
            _signedOut.postValue(true)
        }
    }


}