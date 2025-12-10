package com.po4yka.bitesizereader.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.head
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
            val remoteSize =
                runCatching {
                    client.head("v1/system/db-dump") {
                        timeout {
                            requestTimeoutMillis = 30_000
                            socketTimeoutMillis = 30_000
                        }
                    }.headers[HttpHeaders.ContentLength]?.toLongOrNull()
                }.getOrNull()

            while (true) {
                var existingSize: Long = 0
                if (fileSystem.exists(path)) {
                        existingSize = fileSystem.metadataOrNull(path)?.size ?: 0
                }

                // If we know the remote size and the local file is larger/equal, start fresh
                if (remoteSize != null && existingSize >= remoteSize) {
                    fileSystem.delete(path)
                    existingSize = 0
                }
                val useRange = existingSize > 0 && (remoteSize == null || existingSize < remoteSize)
                // Using prepareGet to handle the stream manually
                client.prepareGet("v1/system/db-dump") {
                    if (useRange) {
                        header(HttpHeaders.Range, "bytes=$existingSize-")
                    }
                    timeout {
                        requestTimeoutMillis = Long.MAX_VALUE
                        socketTimeoutMillis = Long.MAX_VALUE
                    }
                }.execute { response ->
                    if (response.status == HttpStatusCode.RequestedRangeNotSatisfiable) {
                        // Local file likely complete/corrupt for the requested range; wipe and fail fast.
                        if (fileSystem.exists(path)) fileSystem.delete(path)
                        throw io.ktor.client.plugins.ClientRequestException(response, "416")
                    }

                    val contentLength = response.headers[HttpHeaders.ContentLength]?.toLongOrNull()
                    val isPartial = response.status == HttpStatusCode.PartialContent
                    val totalSize =
                        if (isPartial) existingSize + (contentLength ?: 0) else (contentLength ?: 0)

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

                            bytesCopied += read
                            val currentTotal = startByte + bytesCopied
                            emit(DownloadProgress(currentTotal, totalSize))
                        }

                        sink.flush()
                        sink.close()

                        emit(
                            DownloadProgress(
                                startByte + bytesCopied,
                                totalSize,
                                isComplete = true,
                            ),
                        )
                    } catch (e: Exception) {
                        sink.close()
                        throw e
                    }
                }

                // If execute returned normally, we are done
                return@flow
            }
        }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8 * 1024
    }
}
