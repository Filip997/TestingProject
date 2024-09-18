package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.repositories.FirebaseAuthRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val loginRepository = FirebaseAuthRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _resetSuccessful = MutableLiveData(false)
    val resetSuccessful = _resetSuccessful
    private var _resetMessage = MutableLiveData("")
    val resetMessage = _resetMessage

    fun resetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val authResponse = loginRepository.resetPassword(email)
            _resetSuccessful.postValue(authResponse.isSuccessful)
            _resetMessage.postValue(authResponse.message)
            _isLoading.postValue(false)
        }
    }


}