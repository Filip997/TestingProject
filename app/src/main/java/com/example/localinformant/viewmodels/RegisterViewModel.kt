package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.models.LoginRegisterCompanyResponse
import com.example.localinformant.models.LoginRegisterPersonResponse
import com.example.localinformant.models.RegisterCompanyRequest
import com.example.localinformant.models.RegisterPersonRequest
import com.example.localinformant.repositories.FirebaseAuthRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val registerRepository = FirebaseAuthRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _signupSuccessful = MutableLiveData(false)
    val signupSuccessful = _signupSuccessful

    private var _signupMessage = MutableLiveData("")
    val signupMessage = _signupMessage

    private val _registerPersonResponse = MutableLiveData<LoginRegisterPersonResponse>()
    val registerPersonResponse: LiveData<LoginRegisterPersonResponse> = _registerPersonResponse

    private val _registerCompanyResponse = MutableLiveData<LoginRegisterCompanyResponse>()
    val registerCompanyResponse: LiveData<LoginRegisterCompanyResponse> = _registerCompanyResponse

    fun registerPersonWithEmail(request: RegisterPersonRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            /*_isLoading.postValue(true)
            val authResponse = registerRepository.registerPersonWithEmail(request)
            _signupSuccessful.postValue(authResponse.isSuccessful)
            _signupMessage.postValue(authResponse.message)
            _isLoading.postValue(false)*/

            _isLoading.postValue(true)
            val registerResponse = registerRepository.registerPersonWithEmail(request)
            _registerPersonResponse.postValue(registerResponse)
            _isLoading.postValue(false)
        }
    }

    fun registerCompanyWithEmail(request: RegisterCompanyRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            /*_isLoading.postValue(true)
            val authResponse = registerRepository.registerCompanyWithEmail(request)
            _signupSuccessful.postValue(authResponse.isSuccessful)
            _signupMessage.postValue(authResponse.message)
            _isLoading.postValue(false)*/

            _isLoading.postValue(true)
            val registerResponse = registerRepository.registerCompanyWithEmail(request)
            _registerCompanyResponse.postValue(registerResponse)
            _isLoading.postValue(false)
        }
    }

}