package com.example.localinformant.notifications.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.core.presentation.mappers.toUi
import com.example.localinformant.notifications.domain.usecases.GetCurrentUserNotificationsUseCase
import com.example.localinformant.notifications.presentation.models.NotificationsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.listOf

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getCurrentUserNotificationsUseCase: GetCurrentUserNotificationsUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<NotificationsUiState> = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState
        .onStart { getUserNotifications() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NotificationsUiState()
        )

    fun getUserNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            when(val result = getCurrentUserNotificationsUseCase.invoke()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            notifications = result.data.map { it.toUi() }
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            notifications = listOf()
                        )
                    }
                }
            }

            _uiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}