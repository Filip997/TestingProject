package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localinformant.repositories.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel : ViewModel() {

    private val loginRepository = FirebaseAuthRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _loginSuccessful = MutableLiveData(false)
    val loginSuccessful = _loginSuccessful
    private var _loginMessage = MutableLiveData("")
    val loginMessage = _loginMessage
    private val disposables = CompositeDisposable()
    val user: FirebaseUser? by lazy { loginRepository.currentUser() }

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


    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}