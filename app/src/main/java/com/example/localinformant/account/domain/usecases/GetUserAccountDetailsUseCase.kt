package com.example.localinformant.account.domain.usecases

import com.example.localinformant.account.domain.repositories.UserAccountRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.PostWithCompany
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserAccountDetails
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class GetUserAccountDetailsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val globalRepository: GlobalRepository,
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(userId: String?, userType: UserType?): Result<UserAccountDetails, NetworkError> {
        val currentUserType = UserType.valueOf(
            preferencesRepository.getUserType() ?: return Result.Error(
                NetworkError.UNKNOWN
            )
        )

        val currentUser: User? = when(currentUserType) {
            UserType.PERSON -> preferencesRepository.getPerson()
            UserType.COMPANY -> preferencesRepository.getCompany()
        }

        var user: User?
        var finalUserType: UserType
        var isCurrentUser = false
        var isUserFollowed = false

        if (userId != null && userType != null) {
            user = when(userType) {
                UserType.PERSON -> globalRepository.getPersonById(userId)
                UserType.COMPANY -> globalRepository.getCompanyById(userId)
            }

            finalUserType = userType

            isCurrentUser = (currentUserType == userType && currentUser?.let {
                when (it) {
                    is Person -> it.id
                    is Company -> it.id
                    else -> null
                }
            } == userId)

            val currentUserFollowingUserIds = when(currentUser) {
                is Person -> currentUser.following
                is Company -> currentUser.following
                else -> listOf()
            }

            when(user) {
                is Person -> isUserFollowed = currentUserFollowingUserIds.contains(user.id)
                is Company -> isUserFollowed = currentUserFollowingUserIds.contains(user.id)
            }
        } else {
            user = currentUser
            finalUserType = currentUserType
            isCurrentUser = true
            isUserFollowed = false
        }

        return if (user != null) {
            when(user) {
                is Company -> {
                    var posts = listOf<Post>()
                    val postsResult = userAccountRepository.getPostsByUserId(user.id, true)
                    if (postsResult is Result.Success) {
                        posts = postsResult.data
                    }

                    val postsWithCompanies = posts.map {
                        PostWithCompany(
                            post = it,
                            company = user
                        )
                    }

                    val accountDetails = UserAccountDetails(
                        user = user,
                        userType = finalUserType,
                        isCurrentUser = isCurrentUser,
                        isUserFollowed = isUserFollowed,
                        posts = postsWithCompanies
                    )

                    Result.Success(accountDetails)
                }

                is Person -> {
                    var posts = listOf<Post>()
                    val postIdsResult = userAccountRepository.getPostIdsFromUserReactions(user.id)

                    if (postIdsResult is Result.Success) {
                        val postsResult = userAccountRepository.getPostsByPostIds(postIdsResult.data, true)

                        if (postsResult is Result.Success) {
                            posts = postsResult.data
                        }
                    }

                    val postsWithCompanies = posts.map {
                        val result = userAccountRepository.getCompanyByPostId(it.id)

                        PostWithCompany(
                            post = it,
                            company = when(result) {
                                is Result.Error -> null
                                is Result.Success -> result.data
                            }
                        )
                    }

                    val accountDetails = UserAccountDetails(
                        user = user,
                        userType = finalUserType,
                        isCurrentUser = isCurrentUser,
                        isUserFollowed = false,
                        posts = postsWithCompanies
                    )

                    Result.Success(accountDetails)
                }

                else -> Result.Error(NetworkError.UNKNOWN)
            }

        } else {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}