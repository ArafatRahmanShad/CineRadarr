package com.example.data.repository

import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.MediaUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MediaRepository {
    private val _items = MutableStateFlow<List<MediaItem>>(MediaCatalog.items)
    val items: Flow<List<MediaItem>> = _items.asStateFlow()

    fun getAllItems(): List<MediaItem> = _items.value

    fun getItemById(id: String): MediaItem? {
        return _items.value.find { it.id == id }
    }

    fun getLatestUpdates(): List<MediaUpdate> {
        return MediaCatalog.allUpdates
    }

    fun getUpcomingSpotlight(): MediaItem? {
        // Find the closest upcoming release
        val now = System.currentTimeMillis()
        return _items.value
            .filter { it.releaseDateEpoch >= now }
            .minByOrNull { it.releaseDateEpoch }
            ?: _items.value.firstOrNull()
    }

    fun getAllGenres(): List<String> {
        return _items.value
            .flatMap { it.genres }
            .distinct()
            .sorted()
    }

    fun getAllPlatforms(): List<String> {
        return _items.value
            .map { it.platform }
            .distinct()
            .sorted()
    }
}
