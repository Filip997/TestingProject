package com.example.localinformant.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.home.domain.usecases.GetCurrentUserProfileImageUseCase
import com.example.localinformant.core.presentation.mappers.toUi
import com.example.localinformant.home.domain.usecases.GetPostByIdUseCase
import com.example.localinformant.home.domain.usecases.GetPostsByFollowedCompaniesUseCase
import com.example.localinformant.core.domain.usecases.ObserveCommentsUseCase
import com.example.localinformant.core.domain.usecases.ObserveReactionsUseCase
import com.example.localinformant.core.domain.usecases.ObserveUsersUseCase
import com.example.localinformant.core.domain.usecases.SubmitCommentUseCase
import com.example.localinformant.core.domain.usecases.SubmitReactionUseCase
import com.example.localinformant.core.presentation.models.PostUiState
import com.example.localinformant.home.presentation.events.SubmitCommentEvent
import com.example.localinformant.home.presentation.models.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.to

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserProfileImageUseCase: GetCurrentUserProfileImageUseCase,
    private val getPostsByFollowedCompaniesUseCase: GetPostsByFollowedCompaniesUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val submitReactionUseCase: SubmitReactionUseCase,
    private val submitCommentUseCase: SubmitCommentUseCase,
    private val observeUsersUseCase: ObserveUsersUseCase,
    private val observeReactionsUseCase: ObserveReactionsUseCase,
    private val observeCommentsUseCase: ObserveCommentsUseCase
) : ViewModel() {

    private var currentUserProfileImage = ""

    private val _homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private val _visiblePostIds: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val visiblePostIds = _visiblePostIds.asStateFlow()

    private val _submitCommentEvent: MutableSharedFlow<SubmitCommentEvent> = MutableSharedFlow()
    val submitCommentEvent = _submitCommentEvent.asSharedFlow()

    private var isLoadingMore = false
    private var endReached = false

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
                    currentUserProfileImage = getCurrentUserProfileImageUseCase.invoke()

                    _homeUiState.update {
                        it.copy(
                            postUiState = PostUiState(
                                postsUi = postsWithCompany.map { postWithCompany ->
                                    postWithCompany.toUi()
                                },
                                currentUserProfileImage = currentUserProfileImage
                            ),
                            error = null
                        )
                    }

                    observeReactionsForVisiblePosts()
                    observeCommentsForVisiblePosts()
                }

                is Result.Error -> {
                    _homeUiState.update {
                        it.copy(
                            postUiState = PostUiState(
                                postsUi = listOf(),
                                currentUserProfileImage = ""
                            ),
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
                                postUiState = state.postUiState.copy(
                                    postsUi = state.postUiState.postsUi + newPostsWithCompany.map { it.toUi() }
                                ),
                                error = null
                            )
                        }
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

    fun updateVisiblePostIds(postIds: List<String>) {
        if (_visiblePostIds.value == postIds) return
        _visiblePostIds.value = postIds
    }

    fun getPostById(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _homeUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            when(val result = getPostByIdUseCase.invoke(postId)) {
                is Result.Success -> {
                    val postWithCompany = result.data

                    _homeUiState.update {
                        it.copy(
                            postUiState = PostUiState(
                                postsUi = listOf(postWithCompany.toUi()),
                                currentUserProfileImage = currentUserProfileImage
                            ),
                            error = null
                        )
                    }

                    observeReactionsForVisiblePosts()
                    observeCommentsForVisiblePosts()
                }
                is Result.Error -> {
                    _homeUiState.update {
                        it.copy(
                            postUiState = PostUiState(
                                postsUi = listOf(),
                                currentUserProfileImage = ""
                            ),
                            error = result.error
                        )
                    }
                }
            }

            _homeUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    private fun observeReactionsForVisiblePosts() {
        viewModelScope.launch {
            visiblePostIds
                .debounce(300)
                .flatMapLatest { postIds ->
                    if (postIds.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        observeReactionsUseCase(postIds)
                    }
                }
                .flatMapLatest { reactions ->

                    val userTypeIds = reactions
                        .mapNotNull { reaction ->
                            reaction.userId?.let { id ->
                                reaction.userType?.let { type ->
                                    id to type
                                }
                            }
                        }
                        .toMap()

                    if (userTypeIds.isEmpty()) {
                        flowOf(reactions to emptyMap())
                    } else {
                        observeUsersUseCase(userTypeIds)
                            .map { usersList ->
                                val usersMap = usersList.associateBy {
                                    when(it) {
                                        is Person -> it.id
                                        is Company -> it.id
                                        else -> ""
                                    }
                                }
                                reactions to usersMap
                            }
                    }
                }
                .collect { (reactions, usersMap) ->
                    val reactionsByPost = reactions.groupBy { it.postId }

                    _homeUiState.update { currentState ->

                        val updatedPosts = currentState.postUiState.postsUi.map { post ->

                            val updatedReactions = reactionsByPost[post.id]?.map { reaction ->
                                val user = usersMap[reaction.userId]

                                reaction.toUi().copy(
                                    userName = when (user) {
                                        is Person -> user.fullName
                                        is Company -> user.companyName
                                        else -> reaction.userName
                                    },
                                    userProfileImage = when (user) {
                                        is Person -> user.profileImageUrl
                                        is Company -> user.companyProfileImageUrl
                                        else -> reaction.userProfileImage
                                    }
                                )
                            }

                            if (post.postLikes == updatedReactions || updatedReactions == null) {
                                post
                            } else {
                                post.copy(
                                    postLikes = updatedReactions
                                )
                            }
                        }

                        if (currentState.postUiState.postsUi == updatedPosts) {
                            currentState
                        } else {
                            currentState.copy(
                                postUiState = currentState.postUiState.copy(
                                    postsUi = updatedPosts
                                )
                            )
                        }
                    }
                }
        }
    }

    fun submitReaction(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = submitReactionUseCase.invoke(postId)

            if (result is Result.Success) {
                val updatedPosts = _homeUiState.value.postUiState.postsUi.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            likeBtnClicked = true
                        )
                    } else {
                        post
                    }
                }

                _homeUiState.update {
                    it.copy(
                        postUiState = it.postUiState.copy(
                            postsUi = updatedPosts
                        )
                    )
                }
            }
        }
    }

    private fun observeCommentsForVisiblePosts() {
        viewModelScope.launch {
            visiblePostIds
                .debounce(300)
                .flatMapLatest { postIds ->
                    if (postIds.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        observeCommentsUseCase(postIds)
                    }
                }
                .flatMapLatest { comments ->
                    val userTypeIds = comments
                        .mapNotNull { comment ->
                            comment.userId?.let { id ->
                                comment.userType?.let { type ->
                                    id to type
                                }
                            }
                        }
                        .toMap()

                    if (userTypeIds.isEmpty()) {
                        flowOf(comments to emptyMap())
                    } else {
                        observeUsersUseCase(userTypeIds)
                            .map { usersList ->
                                val usersMap = usersList.associateBy {
                                    when(it) {
                                        is Person -> it.id
                                        is Company -> it.id
                                        else -> ""
                                    }
                                }
                                comments to usersMap
                            }
                    }
                }
                .collect { (comments, usersMap) ->
                    val commentsByPost = comments.groupBy { it.postId }

                    _homeUiState.update { currentState ->
                        val updatedPosts = currentState.postUiState.postsUi.map { post ->

                            val updatedComments = commentsByPost[post.id]?.map { comment ->
                                val user = usersMap[comment.userId]

                                comment.toUi().copy(
                                    userName = when (user) {
                                        is Person -> user.fullName
                                        is Company -> user.companyName
                                        else -> comment.userName
                                    },
                                    userProfileImage = when (user) {
                                        is Person -> user.profileImageUrl
                                        is Company -> user.companyProfileImageUrl
                                        else -> comment.userProfileImage
                                    }
                                )
                            }

                            if (post.postComments == updatedComments || updatedComments == null) {
                                post
                            } else {
                                post.copy(
                                    postComments = updatedComments
                                )
                            }
                        }

                        if (currentState.postUiState.postsUi == updatedPosts) {
                            currentState
                        } else {
                            currentState.copy(
                                postUiState = currentState.postUiState.copy(
                                    postsUi = updatedPosts
                                )
                            )
                        }
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
                val updatedPosts = state.postUiState.postsUi.map { post ->
                    if (post.id == postId) {
                        post.copy(commentSectionVisible = !post.commentSectionVisible)
                    } else {
                        post
                    }
                }

                state.copy(
                    postUiState = state.postUiState.copy(
                        postsUi = updatedPosts
                    )
                )
            }
        }
    }

    fun isEndReached(): Boolean = endReached
}