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
import kotlinx.io.readByteArray
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
                    }.takeIf { it.status.value in 200..299 }
                        ?.headers
                        ?.get(HttpHeaders.ContentLength)
                        ?.toLongOrNull()
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
                val etagPath = Path("$outputFile.etag")
                var savedEtag: String? = null
                if (fileSystem.exists(etagPath)) {
                    savedEtag = fileSystem.source(etagPath).buffered()
                        .use { it.readByteArray().decodeToString() }
                }

                val useRange = existingSize > 0 && (remoteSize == null || existingSize < remoteSize)

                val retry =
                    try {
                        client.prepareGet("v1/system/db-dump") {
                            if (useRange) {
                                header(HttpHeaders.Range, "bytes=$existingSize-")
                                if (savedEtag != null) {
                                    header(HttpHeaders.IfRange, savedEtag)
                                }
                            }
                            timeout {
                                requestTimeoutMillis = Long.MAX_VALUE
                                socketTimeoutMillis = Long.MAX_VALUE
                            }
                        }.execute { response ->
                            val contentLength =
                                response.headers[HttpHeaders.ContentLength]?.toLongOrNull()
                            val isPartial = response.status == HttpStatusCode.PartialContent
                            val currentEtag = response.headers[HttpHeaders.ETag]

                            // Save the new ETag if present
                            if (currentEtag != null) {
                                fileSystem.sink(etagPath).buffered()
                                    .use { it.write(currentEtag.encodeToByteArray()) }
                            }

                            val totalSize =
                                if (isPartial) existingSize + (contentLength
                                    ?: 0) else (contentLength ?: 0)

                            // If server didn't accept range (sent 200 OK), reset existingSize to 0 (overwrite)
                            val startByte = if (isPartial) existingSize else 0L

                            if (!isPartial && existingSize > 0) {
                                // Server ignored range (e.g. ETag mismatch); ensure we overwrite from scratch
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

                                // Download complete, clean up ETag file
                                if (fileSystem.exists(etagPath)) {
                                    fileSystem.delete(etagPath)
                                }

                                emit(
                                    DownloadProgress(
                                        startByte + bytesCopied,
                                        totalSize,
                                        isComplete = true,
                                    ),
                                )
                            } catch (e: Exception) {
                                sink.close()
                                // If disk is full, delete the partial file to free up space and prevent resume loops
                                val msg = e.message ?: ""
                                if (msg.contains("ENOSPC") || msg.contains("No space left")) {
                                    if (fileSystem.exists(path)) fileSystem.delete(path)
                                    if (fileSystem.exists(etagPath)) fileSystem.delete(etagPath)
                                }
                                throw e
                            }
                            false
                        }
                    } catch (e: io.ktor.client.plugins.ClientRequestException) {
                        if (e.response.status == HttpStatusCode.RequestedRangeNotSatisfiable) {
                            // Local file likely complete/corrupt for the requested range; wipe and retry.
                            if (fileSystem.exists(path)) fileSystem.delete(path)
                            if (fileSystem.exists(etagPath)) fileSystem.delete(etagPath)
                            true
                        } else {
                            throw e
                        }
                    }

                if (!retry) {
                    return@flow
                }
            }
        }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8 * 1024
    }
}
