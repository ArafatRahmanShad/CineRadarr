package com.example.data.repository

import android.util.Log
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.MediaUpdate
import com.example.data.remote.TmdbNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class MediaRepository {
    private val _items = MutableStateFlow<List<MediaItem>>(MediaCatalog.items)
    val items: Flow<List<MediaItem>> = _items.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: Flow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncStatus = MutableStateFlow<String?>(null)
    val lastSyncStatus: Flow<String?> = _lastSyncStatus.asStateFlow()

    fun getAllItems(): List<MediaItem> = _items.value

    fun getItemById(id: String): MediaItem? {
        return _items.value.find { it.id == id }
    }

    fun getLatestUpdates(): List<MediaUpdate> {
        val tmdbUpdates = _items.value.mapNotNull { it.latestUpdate }
        return (MediaCatalog.allUpdates + tmdbUpdates).distinctBy { it.id }
            .sortedBy { it.timestampDaysAgo }
            .take(15)
    }

    fun getUpcomingSpotlight(): MediaItem? {
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

    suspend fun fetchUpcomingFromTmdb(apiKey: String): Result<Int> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "YOUR_TMDB_API_KEY_HERE") {
            _lastSyncStatus.value = "TMDB API Key not configured. Using verified curated catalog."
            return@withContext Result.failure(IllegalStateException("TMDB API Key missing or placeholder"))
        }

        _isSyncing.value = true
        _lastSyncStatus.value = "Fetching live upcoming movies and series from TMDB..."

        try {
            val movieResponse = TmdbNetworkClient.apiService.getUpcomingMovies(apiKey = apiKey, page = 1)
            val tvResponse = TmdbNetworkClient.apiService.getUpcomingSeries(apiKey = apiKey, page = 1)

            val remoteMovies = movieResponse.results.map { TmdbNetworkClient.mapMovieToMediaItem(it) }
            val remoteTv = tvResponse.results.map { TmdbNetworkClient.mapTvToMediaItem(it) }

            val totalFetched = remoteMovies.size + remoteTv.size

            // Merge with curated catalog items, keeping curated high-profile titles at the top
            val currentList = MediaCatalog.items.toMutableList()
            (remoteMovies + remoteTv).forEach { remoteItem ->
                if (currentList.none { it.title.equals(remoteItem.title, ignoreCase = true) }) {
                    currentList.add(remoteItem)
                }
            }

            // Sort by release date
            currentList.sortBy { it.releaseDateEpoch }
            _items.value = currentList
            _lastSyncStatus.value = "Successfully synced $totalFetched upcoming releases live from TMDB!"
            Result.success(totalFetched)
        } catch (e: Exception) {
            Log.e("MediaRepository", "Failed to fetch from TMDB", e)
            _lastSyncStatus.value = "TMDB Sync Error: ${e.localizedMessage ?: "Network failed"}. Using offline catalog."
            Result.failure(e)
        } finally {
            _isSyncing.value = false
        }
    }
}
