package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SyncRepository

class SyncDataUseCase(private val repository: SyncRepository) {
    suspend operator fun invoke() {
        repository.sync()
    }
}