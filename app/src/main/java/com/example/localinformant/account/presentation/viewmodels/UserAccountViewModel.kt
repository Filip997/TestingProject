package com.example.localinformant.account.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.account.domain.usecases.FollowUnfollowCompanyUseCase
import com.example.localinformant.account.domain.usecases.GetPostsWherePersonCommentedUseCase
import com.example.localinformant.account.domain.usecases.GetPostsWherePersonReactedUseCase
import com.example.localinformant.account.domain.usecases.GetUserAccountDetailsUseCase
import com.example.localinformant.account.domain.usecases.GetUsersByIdsUseCase
import com.example.localinformant.account.domain.usecases.LoadMorePostsByUserIdUseCase
import com.example.localinformant.core.domain.usecases.StartConversationUseCase
import com.example.localinformant.account.domain.usecases.SetProfilePictureUseCase
import com.example.localinformant.account.presentation.events.FollowUnfollowCompanyEvent
import com.example.localinformant.account.presentation.events.OpenFollowersFollowingPopUpWindowEvent
import com.example.localinformant.account.presentation.events.SetProfilePictureEvent
import com.example.localinformant.account.presentation.events.StartConversationEvent
import com.example.localinformant.account.presentation.models.UserAccountUiState
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.core.domain.usecases.ObserveCommentsUseCase
import com.example.localinformant.core.domain.usecases.ObserveReactionsUseCase
import com.example.localinformant.core.domain.usecases.ObserveUsersUseCase
import com.example.localinformant.core.domain.usecases.SubmitCommentUseCase
import com.example.localinformant.core.domain.usecases.SubmitReactionUseCase
import com.example.localinformant.core.presentation.mappers.toFollowerFollowingUi
import com.example.localinformant.core.presentation.mappers.toUi
import com.example.localinformant.core.presentation.models.FollowerFollowingUserUi
import com.example.localinformant.core.presentation.models.UserAccountDetailsUi
import com.example.localinformant.home.presentation.events.SubmitCommentEvent
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
import kotlin.collections.map

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val getUsersByIdsUseCase: GetUsersByIdsUseCase,
    private val setProfilePictureUseCase: SetProfilePictureUseCase,
    private val followUnfollowCompanyUseCase: FollowUnfollowCompanyUseCase,
    private val startConversationUseCase: StartConversationUseCase,
    private val getUserAccountDetailsUseCase: GetUserAccountDetailsUseCase,
    private val loadMorePostsByUserIdUseCase: LoadMorePostsByUserIdUseCase,
    private val getPostsWherePersonReactedUseCase: GetPostsWherePersonReactedUseCase,
    private val getPostsWherePersonCommentedUseCase: GetPostsWherePersonCommentedUseCase,
    private val observeUsersUseCase: ObserveUsersUseCase,
    private val observeReactionsUseCase: ObserveReactionsUseCase,
    private val observeCommentsUseCase: ObserveCommentsUseCase,
    private val submitReactionUseCase: SubmitReactionUseCase,
    private val submitCommentUseCase: SubmitCommentUseCase
) : ViewModel() {

    private val _userAccountUiState: MutableStateFlow<UserAccountUiState> = MutableStateFlow(UserAccountUiState())
    val userAccountUiState = _userAccountUiState.asStateFlow()

    private val _visiblePostIds: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val visiblePostIds = _visiblePostIds.asStateFlow()

    private val _openFollowersPopUpEvent: MutableSharedFlow<OpenFollowersFollowingPopUpWindowEvent> = MutableSharedFlow()
    val openFollowersPopUpEvent = _openFollowersPopUpEvent.asSharedFlow()

    private val _openFollowingPopUpEvent: MutableSharedFlow<OpenFollowersFollowingPopUpWindowEvent> = MutableSharedFlow()
    val openFollowingPopUpEvent = _openFollowingPopUpEvent.asSharedFlow()

    private val _submitCommentEvent: MutableSharedFlow<SubmitCommentEvent> = MutableSharedFlow()
    val submitCommentEvent = _submitCommentEvent.asSharedFlow()

    private val _setProfilePictureEvent: MutableSharedFlow<SetProfilePictureEvent> = MutableSharedFlow()
    val setProfilePictureEvent = _setProfilePictureEvent.asSharedFlow()

    private val _followUnfollowCompanyEvent: MutableSharedFlow<FollowUnfollowCompanyEvent> = MutableSharedFlow()
    val followUnfollowCompanyEvent = _followUnfollowCompanyEvent.asSharedFlow()

    private val _startConversationEvent: MutableSharedFlow<StartConversationEvent> = MutableSharedFlow()
    val startConversationEvent = _startConversationEvent.asSharedFlow()

    private var isLoadingMore = false
    private var endReached = false

    fun getUserAccountDetails(userId: String? = null, userType: UserType? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _userAccountUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            when(val result = getUserAccountDetailsUseCase.invoke(userId, userType)) {
                is Result.Success -> {
                    val userAccountDetails = result.data

                    _userAccountUiState.update {
                        it.copy(
                            userAccountDetails = userAccountDetails.toUi(),
                            error = null
                        )
                    }

                    observeReactionsForVisiblePosts()
                    observeCommentsForVisiblePosts()
                }
                is Result.Error -> {
                    _userAccountUiState.update {
                        it.copy(
                            userAccountDetails = UserAccountDetailsUi(),
                            error = result.error
                        )
                    }
                }
            }

            endReached = false

            _userAccountUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun updateVisiblePostIds(postIds: List<String>) {
        if (_visiblePostIds.value == postIds) return
        _visiblePostIds.value = postIds
    }

    fun setProfilePicture(imageUri: Uri)  {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = setProfilePictureUseCase.invoke(imageUri)) {
                is Result.Success -> {
                    val profileImage = result.data

                    _userAccountUiState.update {
                        it.copy(
                            userAccountDetails = it.userAccountDetails.copy(
                                 userProfileImage = profileImage
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _setProfilePictureEvent.emit(SetProfilePictureEvent.ShowError(result.error))
                }
            }
        }
    }

    fun openFollowersPopUp(userIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = getUsersByIdsUseCase(userIds)) {
                is Result.Success -> {
                    val users = result.data

                    _openFollowersPopUpEvent.emit(
                        OpenFollowersFollowingPopUpWindowEvent.Success(
                            users.map {
                                when(it) {
                                    is Person -> it.toFollowerFollowingUi()
                                    is Company -> it.toFollowerFollowingUi()
                                    else -> FollowerFollowingUserUi()
                                }
                            }
                        )
                    )
                }
                is Result.Error -> {
                    _openFollowersPopUpEvent.emit(
                        OpenFollowersFollowingPopUpWindowEvent.ShowError(result.error)
                    )
                }
            }
        }
    }

    fun openFollowingPopUp(userIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = getUsersByIdsUseCase(userIds)) {
                is Result.Success -> {
                    val users = result.data

                    _openFollowingPopUpEvent.emit(
                        OpenFollowersFollowingPopUpWindowEvent.Success(
                            users.map {
                                when(it) {
                                    is Person -> it.toFollowerFollowingUi()
                                    is Company -> it.toFollowerFollowingUi()
                                    else -> FollowerFollowingUserUi()
                                }
                            }
                        )
                    )
                }
                is Result.Error -> {
                    _openFollowingPopUpEvent.emit(
                        OpenFollowersFollowingPopUpWindowEvent.ShowError(result.error)
                    )
                }
            }
        }
    }

    fun followUnfollowCompany(userId: String?, shouldFollow: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _userAccountUiState.update {
                it.copy(
                    userAccountDetails = it.userAccountDetails.copy(
                        isUserFollowed = shouldFollow
                    ),
                    isLoadingFollowRequest = true
                )
            }

            when(val result = followUnfollowCompanyUseCase.invoke(userId, shouldFollow)) {
                is Result.Success -> {

                }
                is Result.Error -> {
                    _userAccountUiState.update {
                        it.copy(
                            userAccountDetails = it.userAccountDetails.copy(
                                isUserFollowed = !shouldFollow
                            )
                        )
                    }

                    _followUnfollowCompanyEvent.emit(FollowUnfollowCompanyEvent.ShowError(result.error))
                }
            }

            _userAccountUiState.update {
                it.copy(
                    isLoadingFollowRequest = false
                )
            }
        }
    }

    fun startConversation(userId: String, userType: UserType) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = startConversationUseCase.invoke(userId, userType)) {
                is Result.Success -> {
                    val conversationId = result.data

                    _startConversationEvent.emit(
                        StartConversationEvent.Success(conversationId)
                    )
                }

                is Result.Error -> {
                    _startConversationEvent.emit(
                        StartConversationEvent.ShowError(result.error)
                    )
                }
            }
        }
    }

    fun loadMoreCompanyPosts(userId: String?) {
        if (isLoadingMore || endReached) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoadingMore = true

            when (val result = loadMorePostsByUserIdUseCase.invoke(userId, isRefreshing = false)) {
                is Result.Success -> {
                    val newPostsWithCompany = result.data

                    if (newPostsWithCompany.isNotEmpty()) {
                        _userAccountUiState.update { state ->
                            state.copy(
                                userAccountDetails = state.userAccountDetails.copy(
                                    postsUi = state.userAccountDetails.postsUi + newPostsWithCompany.map { it.toUi() }
                                ),
                                error = null
                            )
                        }
                    } else {
                        endReached = true
                    }
                }

                is Result.Error -> {
                    _userAccountUiState.update { it.copy(error = result.error) }
                }
            }

            isLoadingMore = false
        }
    }

    fun getPostsWherePersonReacted(userId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = getPostsWherePersonReactedUseCase.invoke(userId, true)) {
                is Result.Success -> {
                    val postsWherePersonReacted = result.data

                    _userAccountUiState.update { state ->
                        state.copy(
                            userAccountDetails = state.userAccountDetails.copy(
                                postsUi = postsWherePersonReacted.map { it.toUi() }
                            ),
                            error = null
                        )
                    }

                    observeReactionsForVisiblePosts()
                    observeCommentsForVisiblePosts()
                }
                is Result.Error -> {
                    _userAccountUiState.update { state ->
                        state.copy(
                            userAccountDetails = state.userAccountDetails.copy(
                                postsUi = listOf()
                            )
                        )
                    }
                }
            }

            endReached = false
        }
    }

    fun loadMorePostsWherePersonReacted(userId: String?) {
        if (isLoadingMore || endReached) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoadingMore = true

            when(val result = getPostsWherePersonReactedUseCase.invoke(userId, false)) {
                is Result.Success -> {
                    val newPostsWherePersonReacted = result.data

                    if (newPostsWherePersonReacted.isNotEmpty()) {
                        _userAccountUiState.update { state ->
                            state.copy(
                                userAccountDetails = state.userAccountDetails.copy(
                                    postsUi = state.userAccountDetails.postsUi + newPostsWherePersonReacted.map { it.toUi() }
                                ),
                                error = null
                            )
                        }
                    } else {
                        endReached = true
                    }
                }
                is Result.Error -> {
                    _userAccountUiState.update { state ->
                        state.copy(
                            error = result.error
                        )
                    }
                }
            }

            isLoadingMore = false
        }
    }

    fun getPostsWherePersonCommented(userId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = getPostsWherePersonCommentedUseCase.invoke(userId, true)) {
                is Result.Success -> {
                    val postsWherePersonCommented = result.data

                    _userAccountUiState.update { state ->
                        state.copy(
                            userAccountDetails = state.userAccountDetails.copy(
                                postsUi = postsWherePersonCommented.map { it.toUi() }
                            ),
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _userAccountUiState.update { state ->
                        state.copy(
                            userAccountDetails = state.userAccountDetails.copy(
                                postsUi = listOf()
                            )
                        )
                    }
                }
            }

            endReached = false
        }
    }

    fun loadMorePostsWherePersonCommented(userId: String?) {
        if (isLoadingMore || endReached) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoadingMore = true

            when(val result = getPostsWherePersonCommentedUseCase.invoke(userId, false)) {
                is Result.Success -> {
                    val newPostsWherePersonCommented = result.data

                    if (newPostsWherePersonCommented.isNotEmpty()) {
                        _userAccountUiState.update { state ->
                            state.copy(
                                userAccountDetails = state.userAccountDetails.copy(
                                    postsUi = state.userAccountDetails.postsUi + newPostsWherePersonCommented.map { it.toUi() }
                                ),
                                error = null
                            )
                        }
                    } else {
                        endReached = true
                    }
                }
                is Result.Error -> {
                    _userAccountUiState.update { state ->
                        state.copy(
                            error = result.error
                        )
                    }
                }
            }

            isLoadingMore = false
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

                    _userAccountUiState.update { currentState ->

                        val updatedPosts = currentState.userAccountDetails.postsUi.map { post ->

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

                        if (currentState.userAccountDetails.postsUi == updatedPosts) {
                            currentState
                        } else {
                            currentState.copy(
                                userAccountDetails = currentState.userAccountDetails.copy(
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
                val updatedPosts = _userAccountUiState.value.userAccountDetails.postsUi.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            likeBtnClicked = true
                        )
                    } else {
                        post
                    }
                }

                _userAccountUiState.update {
                    it.copy(
                        userAccountDetails = it.userAccountDetails.copy(
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

                    _userAccountUiState.update { currentState ->
                        val updatedPosts = currentState.userAccountDetails.postsUi.map { post ->
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

                        if (currentState.userAccountDetails.postsUi == updatedPosts) {
                            currentState
                        } else {
                            currentState.copy(
                                userAccountDetails = currentState.userAccountDetails.copy(
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
            _userAccountUiState.update { state ->
                val updatedPosts = state.userAccountDetails.postsUi.map { post ->
                    if (post.id == postId) {
                        post.copy(commentSectionVisible = !post.commentSectionVisible)
                    } else {
                        post
                    }
                }

                state.copy(
                    userAccountDetails = state.userAccountDetails.copy(
                        postsUi = updatedPosts
                    )
                )
            }
        }
    }

    fun isEndReached(): Boolean = endReached
}