package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.CollectionAclEntry
import com.po4yka.bitesizereader.data.remote.dto.CollectionDto
import com.po4yka.bitesizereader.data.remote.dto.CollectionInviteResponse
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemDto
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.domain.model.CollectionType
import com.po4yka.bitesizereader.domain.model.Summary
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Clock
import kotlin.time.Instant

fun CollectionDto.toDomain(): Collection {
    return Collection(
        id = id.toString(),
        name = name,
        count = itemCount ?: 0,
        iconName = null, // API doesn't provide icon, use default
        type = CollectionType.User,
        description = description,
        isPublic = isPublic,
        ownerId = ownerId,
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

fun CollectionItemDto.toSummary(): Summary? {
    val summaryDto = summary ?: return null
    val jsonPayload = summaryDto.jsonPayload?.jsonObject

    val title = jsonPayload?.stringValue("title") ?: "Untitled"
    val content = jsonPayload?.stringValue("summary_250") ?: ""
    val sourceUrl = jsonPayload?.stringValue("url") ?: ""
    val tags = jsonPayload?.getStringList("topic_tags") ?: emptyList()

    return Summary(
        id = summaryId.toString(),
        title = title,
        content = content,
        sourceUrl = sourceUrl,
        imageUrl = null,
        createdAt = runCatching { Instant.parse(summaryDto.createdAt) }.getOrElse { Clock.System.now() },
        isRead = summaryDto.isRead,
        tags = tags,
    )
}

private fun JsonObject.stringValue(key: String): String? = (this[key] as? JsonPrimitive)?.contentOrNull

private fun JsonObject.getStringList(key: String): List<String> {
    val element = this[key] ?: return emptyList()
    return runCatching {
        element.jsonArray.mapNotNull { it.jsonPrimitive.contentOrNull }
    }.getOrElse { emptyList() }
}
