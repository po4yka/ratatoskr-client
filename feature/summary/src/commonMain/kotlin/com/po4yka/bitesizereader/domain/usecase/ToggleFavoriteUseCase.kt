package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Factory

@Factory
class ToggleFavoriteUseCase(
    private val repository: SummaryRepository,
) {
    private val mutex = Mutex()
    private val inFlightIds = mutableSetOf<String>()

    suspend operator fun invoke(id: String) {
        val acquired = mutex.withLock { inFlightIds.add(id) }
        if (!acquired) return
        try {
            repository.toggleFavoriteWithSync(id)
        } finally {
            mutex.withLock { inFlightIds.remove(id) }
        }
    }
}
