package com.example.localinformant.auth.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.auth.data.repositories.FirebaseAuthRepositoryImpl
import com.example.localinformant.auth.domain.usecases.GetUserTypeUseCase
import com.example.localinformant.auth.domain.usecases.LoginUserUseCase
import com.example.localinformant.auth.domain.error.AuthError
import com.example.localinformant.auth.domain.validators.LoginValidator
import com.example.localinformant.auth.presentation.events.LoginEvent
import com.example.localinformant.auth.presentation.models.LoginUiState
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.core.domain.models.UserType
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepositoryImpl,
    private val loginValidator: LoginValidator,
    private val getUserTypeUseCase: GetUserTypeUseCase,
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private var userType: UserType? = null

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginUiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState
        .onStart { loadUserType() }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000L),
            LoginUiState()
        )

    private val _loginEvent: MutableSharedFlow<LoginEvent> = MutableSharedFlow()
    val loginEvent = _loginEvent.asSharedFlow()

    private val _userDeleted = MutableLiveData(false)
    val userDeleted = _userDeleted

    private val _changeSuccessful = MutableLiveData(false)
    val changeSuccessful = _changeSuccessful

    private var _changePasswordMessage = MutableLiveData("")
    val changePasswordMessage = _changePasswordMessage

    val user: FirebaseUser? by lazy { authRepository.currentUser() }


    private fun loadUserType() {
        viewModelScope.launch(Dispatchers.IO) {
            userType = getUserTypeUseCase.invoke()
            _loginUiState.update {
                it.copy(
                    userType = userType
                )
            }
        }
    }

    fun onLoginClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _loginUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            val uiState = _loginUiState.value

            when(val loginResult = loginUserUseCase.invoke(uiState.email!!, uiState.password!!, userType!!)) {
                is Result.Success -> {
                    _loginEvent.emit(LoginEvent.NavigateToHome)
                }
                is Result.Error -> {
                    _loginEvent.emit(LoginEvent.ShowError(loginResult.error as AuthError))
                }
            }

            _loginUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun onEmailChange(email: String) {
        viewModelScope.launch {
            val emailError = loginValidator.validateEmail(email)
            _loginUiState.update {
                val passwordError = it.passwordError
                val isLoginEnabled = emailError == null && passwordError == null

                it.copy(
                    email = email,
                    emailError = emailError,
                    isLoginEnabled = isLoginEnabled
                )
            }
        }
    }

    fun onPasswordChange(password: String) {
        viewModelScope.launch {
            val passwordError = loginValidator.validatePassword(password)
            _loginUiState.update {
                val emailError = it.emailError
                val isLoginEnabled = emailError == null && passwordError == null

                it.copy(
                    password = password,
                    passwordError = passwordError,
                    isLoginEnabled = isLoginEnabled
                )
            }
        }
    }

    fun deleteUser(userType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            user?.uid?.let {
                authRepository.deleteUserData(it,userType)
            }
            val authResponse = authRepository.deleteUser()
            _userDeleted.postValue(authResponse.isSuccessful)
            _isLoading.postValue(false)
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val authResponse = authRepository.changePassword(oldPassword, newPassword)
            _changeSuccessful.postValue(authResponse.isSuccessful)
            _changePasswordMessage.postValue(authResponse.message)
            _isLoading.postValue(false)
        }
    }
}