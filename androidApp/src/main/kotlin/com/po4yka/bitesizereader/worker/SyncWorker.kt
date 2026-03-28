package com.po4yka.bitesizereader.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.po4yka.bitesizereader.feature.sync.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.util.error.AppError
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val logger = KotlinLogging.logger {}

class SyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {
    private val syncDataUseCase: SyncDataUseCase by inject()

    @Suppress("TooGenericExceptionCaught")
    override suspend fun doWork(): Result {
        return try {
            syncDataUseCase()
            Result.success()
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: AppError.SessionExpiredError) {
            // Auth is broken — retrying won't help until the user re-authenticates.
            logger.warn(e) { "Sync worker stopped: session expired, user must re-authenticate" }
            Result.failure()
        } catch (e: AppError.NetworkError) {
            logger.warn(e) { "Sync worker: network unavailable, will retry with backoff" }
            Result.retry()
        } catch (e: AppError.TimeoutError) {
            logger.warn(e) { "Sync worker: request timed out, will retry with backoff" }
            Result.retry()
        } catch (e: Exception) {
            logger.error(e) { "Sync worker failed, will retry with backoff" }
            Result.retry()
        }
    }
}
