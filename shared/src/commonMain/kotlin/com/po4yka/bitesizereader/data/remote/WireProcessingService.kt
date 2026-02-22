package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.domain.ProcessingService
import com.po4yka.bitesizereader.domain.model.DomainProcessingUpdate
import com.po4yka.bitesizereader.domain.model.ProcessingStage
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.grpc.processing.GrpcProcessingServiceClient
import com.po4yka.bitesizereader.grpc.processing.ProcessingStatus
import com.po4yka.bitesizereader.grpc.processing.ProcessingStage as GrpcProcessingStage
import com.po4yka.bitesizereader.grpc.processing.SubmitUrlRequest
import com.squareup.wire.GrpcClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
    ): Flow<DomainProcessingUpdate> {
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
        }.map { update ->
            DomainProcessingUpdate(
                status = mapStatus(update.status),
                stage = mapStage(update.stage),
                progress = update.progress,
                message = update.message,
                error = update.error,
            )
        }
    }

    private fun mapStatus(grpcStatus: ProcessingStatus): RequestStatus {
        return when (grpcStatus) {
            ProcessingStatus.PROCESSING_STATUS_PENDING -> RequestStatus.PENDING
            ProcessingStatus.PROCESSING_STATUS_PROCESSING -> RequestStatus.PROCESSING
            ProcessingStatus.PROCESSING_STATUS_COMPLETED -> RequestStatus.COMPLETED
            ProcessingStatus.PROCESSING_STATUS_FAILED -> RequestStatus.FAILED
            else -> RequestStatus.PENDING
        }
    }

    private fun mapStage(grpcStage: GrpcProcessingStage): ProcessingStage {
        return when (grpcStage) {
            GrpcProcessingStage.PROCESSING_STAGE_QUEUED -> ProcessingStage.QUEUED
            GrpcProcessingStage.PROCESSING_STAGE_EXTRACTION -> ProcessingStage.EXTRACTION
            GrpcProcessingStage.PROCESSING_STAGE_SUMMARIZATION -> ProcessingStage.SUMMARIZATION
            GrpcProcessingStage.PROCESSING_STAGE_SAVING -> ProcessingStage.SAVING
            GrpcProcessingStage.PROCESSING_STAGE_DONE -> ProcessingStage.DONE
            else -> ProcessingStage.UNSPECIFIED
        }
    }
}
