package com.po4yka.bitesizereader.domain.model

/**
 * Domain model representing an authenticated user.
 */
data class User(
    val id: Long,
    val username: String?,
    val firstName: String?,
    val lastName: String? = null,
    val photoUrl: String? = null,
    val isOwner: Boolean = false
) {
    val displayName: String
        get() = when {
            firstName != null && lastName != null -> "$firstName $lastName"
            firstName != null -> firstName
            username != null -> "@$username"
            else -> "User $id"
        }
}
