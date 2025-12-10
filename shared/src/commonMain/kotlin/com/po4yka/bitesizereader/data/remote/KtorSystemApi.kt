package com.po4yka.bitesizereader.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.koin.core.annotation.Single

@Suppress("unused")
@Single
class KtorSystemApi(private val client: HttpClient) : SystemApi {
    override suspend fun downloadDatabase(outputFile: String): Flow<DownloadProgress> =
        flow {
            val path = Path(outputFile)
            val fileSystem = SystemFileSystem
            val maxRetries = 5
            var currentRetry = 0

            while (true) {
                var existingSize: Long = 0
                if (fileSystem.exists(path)) {
                    existingSize = fileSystem.metadataOrNull(path)?.size ?: 0
                }

                try {
                    // Using prepareGet to handle the stream manually
                    client.prepareGet("v1/system/db-dump") {
                        if (existingSize > 0) {
                            header(HttpHeaders.Range, "bytes=$existingSize-")
                        }
                        timeout {
                            requestTimeoutMillis = Long.MAX_VALUE
                            socketTimeoutMillis = Long.MAX_VALUE
                        }
                    }.execute { response ->
                        val contentLength =
                            response.headers[HttpHeaders.ContentLength]?.toLongOrNull()
                        val isPartial = response.status == HttpStatusCode.PartialContent
                        val totalSize =
                            if (isPartial) existingSize + (contentLength ?: 0) else (contentLength
                                ?: 0)

                        // If server didn't accept range (sent 200 OK), reset existingSize to 0 (overwrite)
                        val startByte = if (isPartial) existingSize else 0L

                        if (!isPartial && existingSize > 0) {
                            // Server ignored range; ensure we overwrite from scratch
                            fileSystem.delete(path)
                        }

                        val channel: ByteReadChannel = response.bodyAsChannel()
                        val sink = fileSystem.sink(path, append = isPartial).buffered()

                        try {
                            var bytesCopied: Long = 0
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

                            while (!channel.isClosedForRead) {
                                val read = channel.readAvailable(buffer, 0, buffer.size)
                                if (read == -1) break

                                sink.write(buffer, 0, read)
                                // sink.flush() // BufferedSink auto-flushes periodically or on close

                                bytesCopied += read
                                val currentTotal = startByte + bytesCopied
                                emit(DownloadProgress(currentTotal, totalSize))
                            }


                            // Flush and close the sink BEFORE emitting completion to ensure data is safely on disk.
                            sink.flush()
                            sink.close()

                            // If we got here, download is complete
                            emit(
                                DownloadProgress(
                                    startByte + bytesCopied,
                                    totalSize,
                                    isComplete = true,
                                ),
                            )
                            return@execute // Successfully finished
                        } catch (e: Exception) {
                            // If an error occurs (e.g. ENOSPC during write/close), ensure sink is closed.
                            // We don't propagate the exception here immediately if we want the outer loop to handle it?
                            // Actually, outer loop catches 'Exception'. So we just rethrow or let it bubble.
                            sink.close()
                            throw e
                        }
                    }
                    // If execute returned normally, we are done
                    return@flow
                } catch (e: io.ktor.client.plugins.ClientRequestException) {
                   if (e.response.status.value == 416) {
                        // 416 means our local file is likely fully downloaded or invalid (bad range).
                        // Delete and retry from scratch.
                        fileSystem.delete(path)
                        continue
                   }
                   throw e
                } catch (e: Exception) {
                    // Check if it's a network/IO error we should retry
                    val isRetryable =
                        e is kotlinx.io.IOException || // Changed from io.ktor.utils.io.errors.IOException
                                e is io.ktor.client.plugins.HttpRequestTimeoutException ||
                                e is io.ktor.client.network.sockets.SocketTimeoutException ||
                                e is io.ktor.client.network.sockets.ConnectTimeoutException

                    if (isRetryable && currentRetry < maxRetries) {
                        currentRetry++
                        val delayMs = 1000L * (1 shl (currentRetry - 1)) // 1s, 2s, 4s, 8s, 16s
                        val delaySec = delayMs / 1000
                        println(
                            "Download failed: ${e.message}. " +
                                    "Retrying in ${delaySec}s (Attempt $currentRetry/$maxRetries)",
                        )
                        kotlinx.coroutines.delay(delayMs)
                    } else {
                        throw e // Rethrow if not retryable or max retries reached
                    }
                }
            }
        }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8 * 1024
    }
}
