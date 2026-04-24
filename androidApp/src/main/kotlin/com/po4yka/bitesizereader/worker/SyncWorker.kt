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
        } catch (e: AppError.RateLimitError) {
            logger.warn(e) { "Sync worker: rate limited, will retry with backoff" }
            Result.retry()
        } catch (e: AppError.ServerError) {
            if (e.code == HTTP_REQUEST_TIMEOUT || e.code == HTTP_TOO_MANY_REQUESTS || e.code >= HTTP_SERVER_ERROR) {
                logger.warn(e) { "Sync worker: retryable server error ${e.code}, will retry with backoff" }
                Result.retry()
            } else {
                logger.warn(e) { "Sync worker stopped: non-retryable server error ${e.code}" }
                Result.failure()
            }
        } catch (e: AppError.AuthError) {
            logger.warn(e) { "Sync worker stopped: authentication failed" }
            Result.failure()
        } catch (e: AppError.ValidationError) {
            logger.warn(e) { "Sync worker stopped: validation failed" }
            Result.failure()
        } catch (e: AppError.NotFoundError) {
            logger.warn(e) { "Sync worker stopped: resource not found" }
            Result.failure()
        } catch (e: AppError.ConflictError) {
            logger.warn(e) { "Sync worker stopped: unresolved conflict" }
            Result.failure()
        } catch (e: Exception) {
            logger.error(e) { "Sync worker stopped: non-retryable sync failure" }
            Result.failure()
        }
    }

    private companion object {
        const val HTTP_REQUEST_TIMEOUT = 408
        const val HTTP_TOO_MANY_REQUESTS = 429
        const val HTTP_SERVER_ERROR = 500
    }
}
