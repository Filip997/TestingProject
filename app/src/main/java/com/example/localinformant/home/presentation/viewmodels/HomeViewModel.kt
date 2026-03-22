package com.example.localinformant.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.core.presentation.mappers.toUi
import com.example.localinformant.home.domain.usecases.GetPostsByFollowedCompaniesUseCase
import com.example.localinformant.home.domain.usecases.ObserveCommentsUseCase
import com.example.localinformant.home.domain.usecases.ObserveReactionsUseCase
import com.example.localinformant.home.domain.usecases.SubmitCommentUseCase
import com.example.localinformant.home.domain.usecases.SubmitReactionUseCase
import com.example.localinformant.home.presentation.events.SubmitCommentEvent
import com.example.localinformant.home.presentation.models.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
class HomeViewModel @Inject constructor(
    private val getPostsByFollowedCompaniesUseCase: GetPostsByFollowedCompaniesUseCase,
    private val submitReactionUseCase: SubmitReactionUseCase,
    private val submitCommentUseCase: SubmitCommentUseCase,
    private val observeReactionsUseCase: ObserveReactionsUseCase,
    private val observeCommentsUseCase: ObserveCommentsUseCase
) : ViewModel() {

    private val _homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState
        .onStart { getPosts() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HomeUiState()
        )

    private val _submitCommentEvent: MutableSharedFlow<SubmitCommentEvent> = MutableSharedFlow()
    val submitCommentEvent = _submitCommentEvent.asSharedFlow()

    private var isLoadingMore = false
    private var endReached = false
    private var liveReactionsJob: Job? = null
    private var liveCommentsJob: Job? = null

    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {

            _homeUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            when (val result = getPostsByFollowedCompaniesUseCase.invoke()) {
                is Result.Success -> {
                    val postsWithCompany = result.data

                    _homeUiState.update {
                        it.copy(
                            postsUi = postsWithCompany.map { postWithCompany ->
                                postWithCompany.toUi()
                            },
                            error = null
                        )
                    }

                    observeReactions(postsWithCompany.map {
                        it.post.id
                    })

                    observeComments(postsWithCompany.map {
                        it.post.id
                    })
                }

                is Result.Error -> {
                    _homeUiState.update {
                        it.copy(
                            postsUi = listOf(),
                            error = result.error
                        )
                    }
                }
            }

            endReached = false

            _homeUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun loadMorePosts() {
        if (isLoadingMore || endReached) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoadingMore = true

            when (val result = getPostsByFollowedCompaniesUseCase.invoke(isRefreshing = false)) {
                is Result.Success -> {
                    val newPostsWithCompany = result.data

                    if (newPostsWithCompany.isNotEmpty()) {
                        _homeUiState.update { state ->
                            state.copy(
                                postsUi = state.postsUi + newPostsWithCompany.map { it.toUi() },
                                error = null
                            )
                        }

                        val postsUi = _homeUiState.value.postsUi

                        observeReactions(postsUi.map { it.id })
                        observeComments(postsUi.map { it.id })
                    } else {
                        endReached = true
                    }
                }

                is Result.Error -> {
                    _homeUiState.update { it.copy(error = result.error) }
                }
            }

            isLoadingMore = false
        }
    }

    private fun observeReactions(postIds: List<String>) {
        liveReactionsJob?.cancel()

        liveReactionsJob = viewModelScope.launch(Dispatchers.IO) {
            observeReactionsUseCase(postIds).collect { reactions ->

                val reactionsByPost = reactions.groupBy { it.postId }

                val updatedPosts = _homeUiState.value.postsUi.map { post ->

                    val postLikes = reactionsByPost[post.id]
                        ?.map { it.toUi() }
                        ?: emptyList()

                    post.copy(
                        postLikes = postLikes,
                        commentSectionVisible = post.commentSectionVisible
                    )
                }

                _homeUiState.update {
                    it.copy(postsUi = updatedPosts)
                }
            }
        }
    }

    fun submitReaction(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = submitReactionUseCase.invoke(postId)

            if (result is Result.Success) {
                val updatedPosts = _homeUiState.value.postsUi.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            likeBtnClicked = true
                        )
                    } else {
                        post
                    }
                }

                _homeUiState.update {
                    it.copy(postsUi = updatedPosts)
                }
            }
        }
    }

    private fun observeComments(postIds: List<String>) {
        liveCommentsJob?.cancel()

        liveCommentsJob = viewModelScope.launch(Dispatchers.IO) {
            observeCommentsUseCase(postIds).collect { comments ->

                val commentsByPost = comments.groupBy { it.postId }

                val updatedPosts = _homeUiState.value.postsUi.map { post ->

                    val postComments = commentsByPost[post.id]
                        ?.map { it.toUi() }
                        ?: emptyList()

                    post.copy(
                        postComments = postComments,
                        commentSectionVisible = post.commentSectionVisible
                    )
                }

                _homeUiState.update {
                    it.copy(postsUi = updatedPosts)
                }
            }
        }
    }

    fun submitComment(postId: String, commentText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = submitCommentUseCase.invoke(postId, commentText)) {
                is Result.Success -> _submitCommentEvent.emit(SubmitCommentEvent.ClearCommentText(postId))
                is Result.Error -> _submitCommentEvent.emit(SubmitCommentEvent.ShowError(result.error))
            }
        }
    }

    fun changePostCommentSectionVisibility(postId: String) {
        viewModelScope.launch {
            _homeUiState.update { state ->
                val updatedPosts = state.postsUi.map { post ->
                    if (post.id == postId) {
                        post.copy(commentSectionVisible = !post.commentSectionVisible)
                    } else {
                        post
                    }
                }

                state.copy(postsUi = updatedPosts)
            }
        }
    }

    fun isEndReached(): Boolean = endReached
}