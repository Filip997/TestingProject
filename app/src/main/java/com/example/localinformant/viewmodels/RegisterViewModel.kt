package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localinformant.models.RegisterCompanyRequest
import com.example.localinformant.models.RegisterPersonRequest
import com.example.localinformant.repositories.FirebaseAuthRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RegisterViewModel : ViewModel() {

    private val registerRepository = FirebaseAuthRepository()

//    private val registerUserMutableLiveData = MutableLiveData<RegisterUserResponse>()
//    val registerUserLiveData: LiveData<RegisterUserResponse> = registerUserMutableLiveData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _signupSuccessful = MutableLiveData(false)
    val signupSuccessful = _signupSuccessful
    private var _signupMessage = MutableLiveData("")
    val signupMessage = _signupMessage
    private val disposables = CompositeDisposable()

    fun registerPersonWithEmail(request: RegisterPersonRequest) {

        val disposable = registerRepository.registerPersonWithEmail(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _signupSuccessful.value = true
                _isLoading.value = false
            }, {
                _signupMessage.value = it.localizedMessage?.toString() ?: it.message.toString()
                _signupSuccessful.value = false
                _isLoading.value = false
            })
        disposables.add(disposable)
    }

    fun registerCompanyWithEmail(request: RegisterCompanyRequest) {
        val disposable = registerRepository.registerCompanyWithEmail(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _signupSuccessful.value = true
                _isLoading.value = false
            }, {
                _signupMessage.value = it.localizedMessage?.toString() ?: it.message.toString()
                _signupSuccessful.value = false
                _isLoading.value = false
            })
        disposables.add(disposable)
    }


    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

//    fun registerPersonWithEmailAndPassword(request: RegisterPersonRequest) {
//        viewModelScope.launch {
//            isLoadingMutable.postValue(true)
//            registerRepository.registerPersonWithEmailAndPassword(request, registerUserMutableLiveData)
//            isLoadingMutable.postValue(false)
//        }
//    }
//
//    fun registerCompanyWithEmailAndPassword(request: RegisterCompanyRequest) {
//        viewModelScope.launch {
//            isLoadingMutable.postValue(true)
//            registerRepository.registerCompanyWithEmailAndPassword(request, registerUserMutableLiveData)
//        }
//    }
}