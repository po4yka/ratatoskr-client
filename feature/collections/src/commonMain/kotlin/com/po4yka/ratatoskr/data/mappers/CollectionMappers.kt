package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.Collection as GeneratedCollection
import com.po4yka.ratatoskr.api.generated.models.CollectionAclEntry as GeneratedCollectionAclEntry
import com.po4yka.ratatoskr.api.generated.models.CollectionInviteResponse as GeneratedCollectionInviteResponse
import com.po4yka.ratatoskr.domain.model.Collection
import com.po4yka.ratatoskr.domain.model.CollaboratorRole
import com.po4yka.ratatoskr.domain.model.CollectionAcl
import com.po4yka.ratatoskr.domain.model.CollectionInvite
import com.po4yka.ratatoskr.domain.model.CollectionType

fun GeneratedCollection.toDomain(): Collection =
    Collection(
        id = id.toString(),
        name = name,
        count = itemCount?.toInt() ?: 0,
        iconName = null,
        type = CollectionType.User,
        description = description,
        isShared = isShared,
        parentId = parentId?.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )

fun GeneratedCollectionAclEntry.toDomain(): CollectionAcl =
    CollectionAcl(
        userId = userId?.toInt(),
        role = CollaboratorRole.fromString(role.name),
        status = status.name.lowercase(),
        invitedBy = invitedBy?.toInt(),
        createdAt = createdAt?.toString() ?: "",
        updatedAt = updatedAt?.toString() ?: "",
    )

fun GeneratedCollectionInviteResponse.toDomain(): CollectionInvite =
    CollectionInvite(
        token = token,
        role = CollaboratorRole.fromString(role),
        expiresAt = expiresAt?.toString(),
    )
