package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.domain.ProcessingService
import com.po4yka.ratatoskr.domain.model.DomainProcessingUpdate
import com.po4yka.ratatoskr.domain.model.ProcessingStage
import com.po4yka.ratatoskr.domain.model.RequestStatus
import com.po4yka.ratatoskr.grpc.processing.GrpcProcessingServiceClient
import com.po4yka.ratatoskr.grpc.processing.ProcessingStatus
import com.po4yka.ratatoskr.grpc.processing.ProcessingStage as GrpcProcessingStage
import com.po4yka.ratatoskr.grpc.processing.SubmitUrlRequest
import com.squareup.wire.GrpcClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * gRPC-backed implementation of [ProcessingService] using the Wire code generator.
 *
 * Wire + gRPC is used because [submitUrl] is a server-streaming RPC: the backend pushes
 * incremental [DomainProcessingUpdate] events until processing completes. A plain REST/HTTP
 * approach would require polling; gRPC streaming receives updates with a single long-lived
 * connection. If streaming is ever removed, consider replacing the entire Wire + gRPC stack
 * with a Ktor JSON endpoint to reduce dependency weight.
 *
 * DI note: Koin cannot construct [GrpcProcessingServiceClient] directly (Wire-generated,
 * no Koin annotations). The class takes [GrpcClient] — which Koin can resolve from
 * [com.po4yka.ratatoskr.di.NetworkModule] — and builds the Wire client internally.
 */
@Single
class WireProcessingService(grpcClient: GrpcClient) : ProcessingService {
    private val client = GrpcProcessingServiceClient(grpcClient)

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
