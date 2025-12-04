package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestStatusDto(
    @SerialName("id") val id: String,
    @SerialName("status") val status: String
)
