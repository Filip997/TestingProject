package com.example.localinformant.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.repositories.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val loginRepository = FirebaseAuthRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccessful = MutableLiveData(false)
    val loginSuccessful = _loginSuccessful

    private var _loginMessage = MutableLiveData("")
    val loginMessage = _loginMessage

    private val _signedOut = MutableLiveData(false)
    val signedOut = _signedOut

    private val _userDeleted = MutableLiveData(false)
    val userDeleted = _userDeleted

    private val _changeSuccessful = MutableLiveData(false)
    val changeSuccessful = _changeSuccessful

    private var _changePasswordMessage = MutableLiveData("")
    val changePasswordMessage = _changePasswordMessage

    val user: FirebaseUser? by lazy { loginRepository.currentUser() }
    private val disposables = CompositeDisposable()


    fun login(email: String, password: String) {

        val disposable = loginRepository.loginWithEmail(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _loginSuccessful.value = true
                _isLoading.value = false
            }, {
                _loginMessage.value = it.localizedMessage?.toString() ?: it.message.toString()
                _loginSuccessful.value = false
                _isLoading.value = false
            })
        disposables.add(disposable)
    }

    fun deleteUser(userType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val disposable = loginRepository.deleteUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loginRepository.logout() // logout from firebase auth
                    _signedOut.value = true
                    _userDeleted.value = true
                }, {
                    Log.d(ContentValues.TAG, it.message.toString())
                })
            user?.uid?.let {
                val disposableDeleteData = loginRepository.deleteUserData(it,userType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                    }, {throwable ->
                        Log.d(ContentValues.TAG, throwable.message.toString())
                    })
                disposables.add(disposableDeleteData)
            }
            disposables.add(disposable)
            _isLoading.value = false
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        _isLoading.value = true

        val disposable = loginRepository.changePassword(oldPassword, newPassword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _changeSuccessful.value = true
                _isLoading.value = false
            }, {
                _changePasswordMessage.value = it.message.toString()
                _changeSuccessful.value = false
                _isLoading.value = false
            })

        disposables.add(disposable)
    }


    fun logout() {
        viewModelScope.launch {
            loginRepository.logout()
            _signedOut.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}