package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.domain.ProcessingService
import com.po4yka.bitesizereader.grpc.processing.GrpcProcessingServiceClient
import com.po4yka.bitesizereader.grpc.processing.ProcessingUpdate
import com.po4yka.bitesizereader.grpc.processing.SubmitUrlRequest
import com.squareup.wire.GrpcClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class WireProcessingService(
    private val client: GrpcProcessingServiceClient,
) : ProcessingService {
    constructor(grpcClient: GrpcClient) : this(GrpcProcessingServiceClient(grpcClient))

    override fun submitUrl(
        url: String,
        language: String?,
        forceRefresh: Boolean,
    ): Flow<ProcessingUpdate> {
        val request =
            SubmitUrlRequest(
                url = url,
                language = language ?: "auto",
                force_refresh = forceRefresh,
            )

        return flow {
            coroutineScope {
                val responseChannel = client.SubmitUrl().executeIn(this, request)

                for (item in responseChannel) {
                    emit(item)
                }
            }
        }
    }
}
