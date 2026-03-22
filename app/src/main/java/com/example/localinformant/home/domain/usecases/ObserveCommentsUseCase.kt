package com.example.localinformant.home.domain.usecases

import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.home.domain.repositories.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCommentsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    operator fun invoke(postIds: List<String>): Flow<List<Comment>> {
        return homeRepository.observeComments(postIds)
    }
}