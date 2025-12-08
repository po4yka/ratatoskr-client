package com.po4yka.bitesizereader.domain.model

data class Collection(
    val id: String,
    val name: String,
    val count: Int,
    val iconName: String? = null, // e.g., "folder", "star", etc.
    val type: CollectionType = CollectionType.System
)

enum class CollectionType {
    System, // e.g. "All", "Read Later", "Trash"
    User    // User created folders/tags
}
