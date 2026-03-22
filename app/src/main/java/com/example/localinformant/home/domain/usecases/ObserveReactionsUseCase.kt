package com.example.localinformant.home.domain.usecases

import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.home.domain.repositories.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveReactionsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    operator fun invoke(postIds: List<String>): Flow<List<Reaction>> {
        return homeRepository.observeReactions(postIds)
    }
}