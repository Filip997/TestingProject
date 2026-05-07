package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.repositories.GlobalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCommentsUseCase @Inject constructor(
    private val globalRepository: GlobalRepository
) {

    operator fun invoke(postIds: List<String>): Flow<List<Comment>> {
        return globalRepository.observeComments(postIds)
    }
}