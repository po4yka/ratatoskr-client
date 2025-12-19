package com.po4yka.bitesizereader.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val logger = KotlinLogging.logger {}

class SyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {
    private val syncDataUseCase: SyncDataUseCase by inject()

    override suspend fun doWork(): Result {
        return try {
            syncDataUseCase()
            Result.success()
        } catch (e: Exception) {
            logger.error(e) { "Sync worker failed, will retry" }
            Result.retry()
        }
    }
}
