package com.example.localinformant.account.domain.usecases

import com.example.localinformant.account.domain.repositories.UserAccountRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.PostWithCompany
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class LoadMorePostsByUserIdUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val globalRepository: GlobalRepository,
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(userId: String?, isRefreshing: Boolean): Result<List<PostWithCompany>, NetworkError> {
        val company = if (userId != null) {
            globalRepository.getCompanyById(userId)
        } else {
            preferencesRepository.getCompany()
        }

        return if (company != null) {
            var posts = listOf<Post>()
            val postsResult = userAccountRepository.getPostsByUserId(company.id, isRefreshing)
            if (postsResult is Result.Success) {
                posts = postsResult.data
            }

            val postsWithCompanies = posts.map {
                PostWithCompany(
                    post = it,
                    company = company
                )
            }

            Result.Success(postsWithCompanies)
        } else {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}