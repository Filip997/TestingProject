package com.example.localinformant.main.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.core.domain.usecases.GetUserTypeUseCase
import com.example.localinformant.main.domain.usecases.CreateAPostUseCase
import com.example.localinformant.main.presentation.events.CreatePostEvent
import com.example.localinformant.main.presentation.models.MainUiState
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
class MainViewModel @Inject constructor(
    private val getUserTypeUseCase: GetUserTypeUseCase,
    private val createAPostUseCase: CreateAPostUseCase
) : ViewModel() {

    private var userType: UserType? = null

    private val _mainUiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
    val mainUiState = _mainUiState
        .onStart { loadUserType() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MainUiState()
        )

    private val _createPostEvent: MutableSharedFlow<CreatePostEvent> = MutableSharedFlow()
    val createPostEvent = _createPostEvent.asSharedFlow()

    fun loadUserType() {
        viewModelScope.launch(Dispatchers.IO) {
            userType = getUserTypeUseCase.invoke()
            _mainUiState.update {
                it.copy(
                    userType = userType
                )
            }
        }
    }

    fun createAPost(id: String, postText: String, uriImages: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            _mainUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            when(val result = createAPostUseCase.invoke(id, postText, uriImages)) {
                is Result.Success -> {
                    _createPostEvent.emit(CreatePostEvent.ClosePopUpWindow)
                }
                is Result.Error -> {
                    _createPostEvent.emit(CreatePostEvent.ShowError(result.error))
                }
            }

            _mainUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}