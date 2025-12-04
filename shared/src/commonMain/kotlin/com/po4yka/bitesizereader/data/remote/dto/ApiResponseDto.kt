package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    @SerialName("data") val data: T? = null,
    @SerialName("error") val error: ErrorResponseDto? = null,
    @SerialName("success") val success: Boolean
)