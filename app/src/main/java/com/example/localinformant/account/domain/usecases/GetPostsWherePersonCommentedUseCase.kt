package com.example.localinformant.account.domain.usecases

import com.example.localinformant.account.domain.repositories.UserAccountRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.PostWithCompany
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class GetPostsWherePersonCommentedUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val globalRepository: GlobalRepository,
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(userId: String?, isRefreshing: Boolean): Result<List<PostWithCompany>, NetworkError> {
        val person = if (userId != null) {
            globalRepository.getPersonById(userId)
        } else {
            preferencesRepository.getPerson()
        }

        return if (person != null) {
            var posts = listOf<Post>()
            val postIdsResult = userAccountRepository.getPostIdsFromUserComments(person.id)

            if (postIdsResult is Result.Success) {
                val postsResult = userAccountRepository.getPostsByPostIds(postIdsResult.data, isRefreshing)

                if (postsResult is Result.Success) {
                    posts = postsResult.data
                }
            }

            val postsWithCompanies = posts.map {
                val result = userAccountRepository.getCompanyByPostId(it.id)

                PostWithCompany(
                    post = it,
                    company = when (result) {
                        is Result.Error -> null
                        is Result.Success -> result.data
                    }
                )
            }

            Result.Success(postsWithCompanies)
        } else {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}