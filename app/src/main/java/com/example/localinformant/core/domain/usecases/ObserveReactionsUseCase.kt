package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.repositories.GlobalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveReactionsUseCase @Inject constructor(
    private val globalRepository: GlobalRepository
) {

    operator fun invoke(postIds: List<String>): Flow<List<Reaction>> {
        return globalRepository.observeReactions(postIds)
    }
}