package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localinformant.repositories.FirebaseAuthRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ForgotPasswordViewModel : ViewModel() {

    private val loginRepository = FirebaseAuthRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _resetSuccessful = MutableLiveData(false)
    val resetSuccessful = _resetSuccessful
    private var _resetMessage = MutableLiveData("")
    val resetMessage = _resetMessage
    private val disposables = CompositeDisposable()

    fun resetPassword(email: String) {
        _isLoading.value = true
        val disposable =
            loginRepository.resetPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _resetSuccessful.value = true
                    _isLoading.value = false
                }, {
                    _resetMessage.value = it.message.toString()
                    _resetSuccessful.value = false
                    _isLoading.value = false
                })

        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}