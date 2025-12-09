package com.po4yka.bitesizereader.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.po4yka.bitesizereader.domain.usecase.SyncDataUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
            e.printStackTrace()
            Result.retry()
        }
    }
}
