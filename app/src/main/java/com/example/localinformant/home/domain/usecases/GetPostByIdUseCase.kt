package com.example.localinformant.home.domain.usecases

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.PostWithCompany
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.home.domain.repositories.HomeRepository
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(postId: String): Result<PostWithCompany, NetworkError> {
        return when(val postResult = homeRepository.getPostById(postId)) {
            is Result.Success -> {
                val post = postResult.data
                val companyResult = homeRepository.getCompanyByPostId(postId)

                val postWithCompany = PostWithCompany(
                    post = post,
                    company = when(companyResult) {
                        is Result.Error -> null
                        is Result.Success -> companyResult.data
                    }
                )

                Result.Success(postWithCompany)
            }

            is Result.Error -> postResult
        }
    }
}