package com.po4yka.bitesizereader.domain.model

data class User(
    val id: String,
    val username: String?,
    val displayName: String?,
    val photoUrl: String?,
    val clientId: String? = null,
    val isOwner: Boolean = false,
    val createdAt: String? = null,
)
