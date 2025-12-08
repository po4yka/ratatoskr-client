package com.po4yka.bitesizereader.data.remote

import io.ktor.client.HttpClient
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
import kotlinx.io.files.sink

@Suppress("unused")
class KtorSystemApi(private val client: HttpClient) : SystemApi {

    override suspend fun downloadDatabase(outputFile: String): Flow<DownloadProgress> = flow {
        val path = Path(outputFile)
        val fileSystem = SystemFileSystem

        var existingSize: Long = 0
        if (fileSystem.exists(path)) {
            existingSize = fileSystem.metadataOrNull(path)?.size ?: 0
        }

        // Using prepareGet to handle the stream manually
        client.prepareGet("system/db-dump") {
            if (existingSize > 0) {
                header(HttpHeaders.Range, "bytes=$existingSize-")
            }
        }.execute { response ->
            val contentLength = response.headers[HttpHeaders.ContentLength]?.toLongOrNull()
            // If Range was respected, Content-Length is the *remaining* size.
            // If not (200 OK instead of 206 Partial), it's total size and we overwrite.

            val isPartial = response.status == HttpStatusCode.PartialContent
            val totalSize = if (isPartial) existingSize + (contentLength ?: 0) else (contentLength ?: 0)

            // If server didn't accept range (sent 200 OK), reset existingSize to 0 (overwrite)
            val startByte = if (isPartial) existingSize else 0L

            if (!isPartial && existingSize > 0) {
                // Server ignored range; ensure we overwrite from scratch
                fileSystem.delete(path)
            }

            val channel: ByteReadChannel = response.bodyAsChannel()

            // Open sink. If partial, append. If not, overwrite.
            val sink = fileSystem.sink(path, append = isPartial).buffered()

            try {
                var bytesCopied: Long = 0
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer, 0, buffer.size)
                    if (read == -1) break

                    sink.write(buffer, 0, read)
                    sink.flush() // Flush occasionally or let buffer handle it? BufferedSink handles it.

                    bytesCopied += read
                    val currentTotal = startByte + bytesCopied

                    emit(DownloadProgress(currentTotal, totalSize))
                }
                emit(DownloadProgress(startByte + bytesCopied, totalSize, isComplete = true))
            } finally {
                sink.close()
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8 * 1024
    }
}
