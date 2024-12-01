package com.example.localinformant.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.models.LoginRegisterCompanyResponse
import com.example.localinformant.models.LoginRegisterPersonResponse
import com.example.localinformant.repositories.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val loginRepository = FirebaseAuthRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /*private val _loginSuccessful = MutableLiveData(false)
    val loginSuccessful = _loginSuccessful

    private var _loginMessage = MutableLiveData("")
    val loginMessage = _loginMessage*/

    private var _loginPersonResponse = MutableLiveData<LoginRegisterPersonResponse>()
    val loginPersonResponse = _loginPersonResponse

    private var _loginCompanyResponse = MutableLiveData<LoginRegisterCompanyResponse>()
    val loginCompanyResponse = _loginCompanyResponse

    private val _userDeleted = MutableLiveData(false)
    val userDeleted = _userDeleted

    private val _changeSuccessful = MutableLiveData(false)
    val changeSuccessful = _changeSuccessful

    private var _changePasswordMessage = MutableLiveData("")
    val changePasswordMessage = _changePasswordMessage

    val user: FirebaseUser? by lazy { loginRepository.currentUser() }


    fun loginPerson(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val authResponse = loginRepository.loginPersonWithEmail(email, password)
            _loginPersonResponse.postValue(authResponse)
            /*_loginSuccessful.postValue(authResponse.isSuccessful)
            _loginMessage.postValue(authResponse.message)*/
            _isLoading.postValue(false)
        }
    }

    fun loginCompany(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val authResponse = loginRepository.loginCompanyWithEmail(email, password)
            _loginCompanyResponse.postValue(authResponse)
            /*_loginSuccessful.postValue(authResponse.isSuccessful)
            _loginMessage.postValue(authResponse.message)*/
            _isLoading.postValue(false)
        }
    }

    fun deleteUser(userType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            user?.uid?.let {
                loginRepository.deleteUserData(it,userType)
            }
            val authResponse = loginRepository.deleteUser()
            _userDeleted.postValue(authResponse.isSuccessful)
            _isLoading.postValue(false)
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val authResponse = loginRepository.changePassword(oldPassword, newPassword)
            _changeSuccessful.postValue(authResponse.isSuccessful)
            _changePasswordMessage.postValue(authResponse.message)
            _isLoading.postValue(false)
        }
    }
}