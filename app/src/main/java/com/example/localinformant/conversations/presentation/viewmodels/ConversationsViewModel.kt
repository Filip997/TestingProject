package com.example.localinformant.conversations.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.conversations.domain.usecases.GetChatParticipant2UserTypeUseCase
import com.example.localinformant.conversations.domain.usecases.GetConversationParticipant2UseCase
import com.example.localinformant.conversations.domain.usecases.GetUserConversationsUseCase
import com.example.localinformant.conversations.domain.usecases.LoadMessagesUseCase
import com.example.localinformant.conversations.domain.usecases.ObserveMessagesByConversationIdUseCase
import com.example.localinformant.conversations.domain.usecases.ObserveUserStatusUseCase
import com.example.localinformant.conversations.domain.usecases.SearchUsersUseCase
import com.example.localinformant.conversations.domain.usecases.SendMessageToConversationUseCase
import com.example.localinformant.conversations.presentation.events.CreateNewConversationEvent
import com.example.localinformant.conversations.presentation.events.GetChatParticipant2UserTypeEvent
import com.example.localinformant.conversations.presentation.events.SendMessageEvent
import com.example.localinformant.conversations.presentation.models.ChatUiState
import com.example.localinformant.conversations.presentation.models.ConversationsUiState
import com.example.localinformant.conversations.presentation.models.NewConversationUiState
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.core.domain.usecases.StartConversationUseCase
import com.example.localinformant.core.presentation.mappers.toSearchedUserUi
import com.example.localinformant.core.presentation.mappers.toUi
import com.example.localinformant.core.presentation.models.SearchedUserUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val getUserConversationsUseCase: GetUserConversationsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val startConversationUseCase: StartConversationUseCase,
    private val observeMessagesByConversationIdUseCase: ObserveMessagesByConversationIdUseCase,
    private val observeUserStatusUseCase: ObserveUserStatusUseCase,
    private val getConversationParticipant2UseCase: GetConversationParticipant2UseCase,
    private val loadMessagesUseCase: LoadMessagesUseCase,
    private val getChatParticipant2UserTypeUseCase: GetChatParticipant2UserTypeUseCase,
    private val sendMessageToConversationUseCase: SendMessageToConversationUseCase
) : ViewModel() {

    private val _conversationsUiState: MutableStateFlow<ConversationsUiState> = MutableStateFlow(
        ConversationsUiState()
    )
    val conversationsUiState = _conversationsUiState
        .onStart { loadUserConversations() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ConversationsUiState()
        )

    private val _newConversationUiState: MutableStateFlow<NewConversationUiState> = MutableStateFlow(
        NewConversationUiState()
    )
    val newConversationUiState = _newConversationUiState.asStateFlow()

    private val _chatUiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    private val _createNewConversationEvent: MutableSharedFlow<CreateNewConversationEvent> = MutableSharedFlow()
    val createNewConversationEvent = _createNewConversationEvent.asSharedFlow()

    private val _getChatParticipant2UserTypeEvent: MutableSharedFlow<GetChatParticipant2UserTypeEvent>
    = MutableSharedFlow()
    val getChatParticipant2UserTypeEvent = _getChatParticipant2UserTypeEvent.asSharedFlow()

    private val _sendMessageEvent: MutableSharedFlow<SendMessageEvent> = MutableSharedFlow()
    val sendMessageEvent = _sendMessageEvent.asSharedFlow()

    private var searchUsersJob: Job? = null
    private var isLoadingMore = false
    private var endReached = false

    private fun loadUserConversations() {
        viewModelScope.launch(Dispatchers.IO) {
            _conversationsUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            when (val result = getUserConversationsUseCase.invoke()) {
                is Result.Success -> {
                    val conversations = result.data

                    _conversationsUiState.update { state ->
                        state.copy(
                            conversations = conversations.map { it.toUi() },
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _conversationsUiState.update {
                        it.copy(
                            error = result.error,
                            isLoading = false
                        )
                    }
                }
            }

            _conversationsUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun searchUsersByName(searchQuery: String) {
        searchUsersJob?.cancel()

        searchUsersJob = viewModelScope.launch(Dispatchers.IO) {
            _newConversationUiState.update {
                it.copy(isLoading = true)
            }

            val users = searchUsersUseCase.invoke(searchQuery)
            val searchedUsers: List<SearchedUserUi> = users.map {
                when(it) {
                    is Person -> it.toSearchedUserUi()
                    is Company -> it.toSearchedUserUi()
                    else -> SearchedUserUi()
                }
            }

            _newConversationUiState.update {
                it.copy(
                    searchedUsers = searchedUsers
                )
            }

            _newConversationUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun createNewConversationWithUser(userId: String, userType: UserType) {
        viewModelScope.launch(Dispatchers.IO) {
            _newConversationUiState.update {
                it.copy(isLoading = true)
            }

            when(val result = startConversationUseCase.invoke(userId, userType)) {
                is Result.Success -> {
                    val conversationId = result.data

                    _createNewConversationEvent.emit(
                        CreateNewConversationEvent.Success(userId, conversationId)
                    )
                }
                is Result.Error -> {
                    _createNewConversationEvent.emit(
                        CreateNewConversationEvent.ShowError(result.error)
                    )
                }
            }

            _newConversationUiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun loadConversationChat(participant2Id: String, conversationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = getConversationParticipant2UseCase.invoke(participant2Id)) {
                is Result.Success -> {
                    when(val user = result.data) {
                        is Person -> {
                            _chatUiState.update {
                                it.copy(
                                    participant2Id = user.id,
                                    participant2Name = user.fullName,
                                    participant2ProfileImage = user.profileImageUrl,
                                    error = null
                                )
                            }
                        }
                        is Company -> {
                            _chatUiState.update {
                                it.copy(
                                    participant2Id = user.id,
                                    participant2Name = user.companyName,
                                    participant2ProfileImage = user.companyProfileImageUrl,
                                    error = null
                                )
                            }
                        }
                    }

                    getMessagesForConversation(conversationId)
                    observeUserStatus(participant2Id)
                }
                is Result.Error -> {
                    _chatUiState.update {
                        it.copy(
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    private fun getMessagesForConversation(conversationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _chatUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            when(val result = loadMessagesUseCase.invoke(conversationId)) {
                is Result.Success -> {
                    val messages = result.data

                    _chatUiState.update {
                        it.copy(
                            messages = messages.map { message -> message.toUi() },
                            error = null
                        )
                    }

                    observeLiveMessagesForConversation(conversationId)
                }
                is Result.Error -> {
                    _chatUiState.update {
                        it.copy(
                            error = result.error
                        )
                    }
                }
            }

            endReached = false

            _chatUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun loadMoreMessagesForConversation(conversationId: String) {
        if (isLoadingMore || endReached) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoadingMore = true

            _chatUiState.update {
                it.copy(
                    isLoadingMore = true
                )
            }

            when(val result = loadMessagesUseCase.invoke(conversationId, false)) {
                is Result.Success -> {
                    val messages = result.data

                    if (messages.isNotEmpty()) {
                        _chatUiState.update { state ->
                            state.copy(
                                messages = state.messages + messages.map { message -> message.toUi() },
                                error = null
                            )
                        }
                    } else {
                        endReached = true
                    }
                }
                is Result.Error -> {
                    _chatUiState.update {
                        it.copy(
                            error = result.error
                        )
                    }
                }
            }

            isLoadingMore = false

            _chatUiState.update {
                it.copy(
                    isLoadingMore = false
                )
            }
        }
    }

    fun isEndReached(): Boolean = endReached

    private fun observeLiveMessagesForConversation(conversationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeMessagesByConversationIdUseCase.invoke(conversationId)
                .collect { newMessages ->
                    val finalMessages = (newMessages.map { message -> message.toUi() } + _chatUiState.value.messages).distinct()

                    _chatUiState.update {
                        it.copy(
                            messages = finalMessages
                        )
                    }
                }
        }
    }

    private fun observeUserStatus(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            observeUserStatusUseCase.invoke(userId)
                .collect { status ->
                    _chatUiState.update {
                        it.copy(
                            participant2Status = status
                        )
                    }
                }
        }
    }

    fun goToUserProfile(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _chatUiState.update {
                it.copy(
                    isLoadingGoingToUserProfile = true
                )
            }

            when(val result = getChatParticipant2UserTypeUseCase.invoke(userId)) {
                is Result.Success -> {
                    _getChatParticipant2UserTypeEvent.emit(GetChatParticipant2UserTypeEvent.Success(result.data))
                }
                is Result.Error -> {
                    _getChatParticipant2UserTypeEvent.emit(GetChatParticipant2UserTypeEvent.ShowError(result.error))
                }
            }

            _chatUiState.update {
                it.copy(
                    isLoadingGoingToUserProfile = false
                )
            }
        }
    }

    fun sendMessageToConversation(
        conversationId: String,
        receiverId: String,
        messageText: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = sendMessageToConversationUseCase.invoke(
                conversationId, receiverId, messageText
            )) {
                is Result.Success -> {

                }

                is Result.Error -> {
                    _sendMessageEvent.emit(
                        SendMessageEvent.ShowError(result.error)
                    )
                }
            }
        }
    }
}