package com.po4yka.ratatoskr.domain.model

data class CollectionAcl(
    val userId: Int?,
    val role: CollaboratorRole,
    val status: String,
    val invitedBy: Int? = null,
    val createdAt: String,
    val updatedAt: String,
)

enum class CollaboratorRole {
    Owner,
    Editor,
    Viewer,
    ;

    companion object {
        fun fromString(value: String): CollaboratorRole =
            when (value.lowercase()) {
                "owner" -> Owner
                "editor" -> Editor
                "viewer" -> Viewer
                else -> Viewer
            }
    }

    fun toApiString(): String =
        when (this) {
            Owner -> "owner"
            Editor -> "editor"
            Viewer -> "viewer"
        }
}

data class CollectionInvite(
    val token: String,
    val role: CollaboratorRole,
    val expiresAt: String?,
)
