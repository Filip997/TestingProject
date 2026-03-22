package com.example.localinformant.home.domain.usecases

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.PostWithCompany
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.home.domain.repositories.HomeRepository
import javax.inject.Inject

class GetPostsByFollowedCompaniesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val homeRepository: HomeRepository
) {
    private var followedUsersIds: List<String> = listOf()

    suspend operator fun invoke(isRefreshing: Boolean = true): Result<List<PostWithCompany>, NetworkError> {
        val userType = preferencesRepository.getUserType()

        if (isRefreshing) {
            followedUsersIds = when (UserType.valueOf(userType!!)) {
                UserType.PERSON -> {
                    val person = preferencesRepository.getPerson()
                    person?.following ?: listOf()
                }

                UserType.COMPANY -> {
                    val company = preferencesRepository.getCompany()
                    company?.following?.plus(company.id) ?: listOf()
                }
            }
        }

        val result = homeRepository.getPostsByFollowedCompanies(followedUsersIds, isRefreshing)

        return when (result) {
            is Result.Error -> result
            is Result.Success -> {
                val posts = result.data

                val postWithCompanyList = posts.map { post ->
                    val result = homeRepository.getCompanyByPostId(post.id)

                    PostWithCompany(
                        post = post,
                        company = when(result) {
                            is Result.Error -> null
                            is Result.Success -> result.data
                        }
                    )
                }

                Result.Success(postWithCompanyList)
            }
        }
    }
}