package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollectionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface CollectionRepository {
    fun getCollections(): Flow<List<Collection>>
}

class MockCollectionRepository : CollectionRepository {
    override fun getCollections(): Flow<List<Collection>> {
        return flowOf(
            listOf(
                // System Collections
                Collection("unsorted", "Unsorted", 9, "inbox", CollectionType.System),
                Collection("read_later", "Read Later", 22, "bookmark", CollectionType.System),
                // User Collections (Group 1 - Work/Projects)
                Collection("beautiful_web", "Beautiful Web", 25, "palette", CollectionType.User),
                Collection("inspiration", "Inspiration Board", 79, "lightbulb", CollectionType.User),
                Collection("vacation", "Vacation Ideas", 16, "map", CollectionType.User),
                Collection("food", "Food", 6, "restaurant", CollectionType.User),
                Collection("games", "Games", 0, "sports_esports", CollectionType.User),
                // User Collections (Group 2 - Other)
                Collection("wishboard", "Wishboard", 13, "spa", CollectionType.User),
                Collection("design", "Design", 79, "diamond", CollectionType.User),
                Collection("flat_design", "Flat Design", 10, "architecture", CollectionType.User),
                // Trash
                Collection("trash", "Trash", 0, "delete", CollectionType.System),
            ),
        )
    }
}
