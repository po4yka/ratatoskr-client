package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.CollectionAclEntry
import com.po4yka.bitesizereader.data.remote.dto.CollectionDto
import com.po4yka.bitesizereader.data.remote.dto.CollectionInviteResponse
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.domain.model.CollectionType

fun CollectionDto.toDomain(): Collection {
    return Collection(
        id = id.toString(),
        name = name,
        count = itemCount ?: 0,
        iconName = null,
        type = CollectionType.User,
        description = description,
        isShared = isShared,
        parentId = parentId?.toString(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun CollectionAclEntry.toDomain(): CollectionAcl {
    return CollectionAcl(
        userId = userId,
        role = CollaboratorRole.fromString(role),
        status = status,
        invitedBy = invitedBy,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun CollectionInviteResponse.toDomain(): CollectionInvite {
    return CollectionInvite(
        token = token,
        role = CollaboratorRole.fromString(role),
        expiresAt = expiresAt,
    )
}

// CollectionItem no longer embeds summary data per the OpenAPI spec.
// Items only contain collection_id, summary_id, created_at, position.
// Summary data must be fetched separately by summary_id.
