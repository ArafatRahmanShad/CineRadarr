package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NotificationAlertEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.ReleaseStatus
import com.example.data.model.WatchlistStatus
import com.example.data.repository.MediaCatalog
import com.example.data.repository.MediaRepository
import com.example.data.repository.WatchlistRepository
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String) {
    EXPLORE("Explore"),
    WATCHLIST("Watchlist"),
    TIMELINE("Timeline"),
    ALERTS("Alerts")
}

data class UiState(
    val currentTab: AppNavTab = AppNavTab.EXPLORE,
    val searchQuery: String = "",
    val isSearchOpen: Boolean = false,
    val selectedType: MediaType? = null,
    val selectedGenre: String? = null,
    val selectedStatus: ReleaseStatus? = null,
    val selectedWatchlistStatus: WatchlistStatus = WatchlistStatus.WANT_TO_WATCH,
    val selectedMediaDetail: MediaItem? = null,
    val watchlistConfigTarget: MediaItem? = null,
    val testAlertSentMessage: String? = null
)

class CineRadarViewModel(application: Application) : AndroidViewModel(application) {
    private val mediaRepository = MediaRepository()
    private val watchlistRepository = WatchlistRepository.getInstance(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val allMedia: StateFlow<List<MediaItem>> = mediaRepository.items
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MediaCatalog.items)

    val watchlistItems: StateFlow<List<WatchlistEntity>> = watchlistRepository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<NotificationAlertEntity>> = watchlistRepository.alerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = watchlistRepository.unreadAlertCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isGlobalAlertsEnabled = MutableStateFlow(NotificationHelper.isGlobalAlertsEnabled(application))
    val isGlobalAlertsEnabled: StateFlow<Boolean> = _isGlobalAlertsEnabled.asStateFlow()

    private val _globalReminderLead = MutableStateFlow(NotificationHelper.getGlobalReminderLead(application))
    val globalReminderLead: StateFlow<String> = _globalReminderLead.asStateFlow()

    private val _armedReleasesCount = MutableStateFlow(0)
    val armedReleasesCount: StateFlow<Int> = _armedReleasesCount.asStateFlow()

    init {
        // Automatically ensure all upcoming movie & series releases have push notifications armed
        if (_isGlobalAlertsEnabled.value) {
            val count = NotificationHelper.scheduleAllUpcomingReleases(
                application,
                MediaCatalog.items,
                _globalReminderLead.value
            )
            _armedReleasesCount.value = count
        }

        val apiKey = getEffectiveTmdbKey()
        if (apiKey.isNotBlank()) {
            viewModelScope.launch {
                mediaRepository.fetchUpcomingFromTmdb(apiKey)
            }
        }
    }

    val isTmdbSyncing: StateFlow<Boolean> = mediaRepository.isSyncing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val tmdbSyncStatus: StateFlow<String?> = mediaRepository.lastSyncStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val genres: List<String> = mediaRepository.getAllGenres()
    val platforms: List<String> = mediaRepository.getAllPlatforms()
    val latestUpdates = mediaRepository.getLatestUpdates()
    val spotlightItem: MediaItem? = mediaRepository.getUpcomingSpotlight()

    // Filtered media list based on search, type, genre, and status
    val filteredMedia: StateFlow<List<MediaItem>> = combine(
        allMedia,
        _uiState
    ) { items, state ->
        items.filter { item ->
            val matchesQuery = state.searchQuery.isBlank() ||
                    item.title.contains(state.searchQuery, ignoreCase = true) ||
                    item.directorOrCreator.contains(state.searchQuery, ignoreCase = true) ||
                    item.cast.any { it.contains(state.searchQuery, ignoreCase = true) } ||
                    item.genres.any { it.contains(state.searchQuery, ignoreCase = true) } ||
                    item.platform.contains(state.searchQuery, ignoreCase = true)

            val matchesType = state.selectedType == null || item.type == state.selectedType
            val matchesGenre = state.selectedGenre == null || item.genres.contains(state.selectedGenre)
            val matchesStatus = state.selectedStatus == null || item.status == state.selectedStatus

            matchesQuery && matchesType && matchesGenre && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MediaCatalog.items)

    fun setNavTab(tab: AppNavTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleSearchOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSearchOpen = open,
            searchQuery = if (!open) "" else _uiState.value.searchQuery
        )
    }

    fun setTypeFilter(type: MediaType?) {
        _uiState.value = _uiState.value.copy(selectedType = type)
    }

    fun setGenreFilter(genre: String?) {
        _uiState.value = _uiState.value.copy(
            selectedGenre = if (_uiState.value.selectedGenre == genre) null else genre
        )
    }

    fun setStatusFilter(status: ReleaseStatus?) {
        _uiState.value = _uiState.value.copy(
            selectedStatus = if (_uiState.value.selectedStatus == status) null else status
        )
    }

    fun setWatchlistStatusTab(status: WatchlistStatus) {
        _uiState.value = _uiState.value.copy(selectedWatchlistStatus = status)
    }

    fun openMediaDetail(item: MediaItem?) {
        _uiState.value = _uiState.value.copy(selectedMediaDetail = item)
    }

    fun openWatchlistConfig(item: MediaItem?) {
        _uiState.value = _uiState.value.copy(watchlistConfigTarget = item)
    }

    fun clearFeedbackMessage() {
        _uiState.value = _uiState.value.copy(testAlertSentMessage = null)
    }

    fun addToWatchlist(
        item: MediaItem,
        status: WatchlistStatus = WatchlistStatus.WANT_TO_WATCH,
        notifyEnabled: Boolean = true,
        notifyDaysBefore: Int = 1,
        notes: String = ""
    ) {
        viewModelScope.launch {
            watchlistRepository.addToWatchlist(item, status, notifyEnabled, notifyDaysBefore, notes)
            _uiState.value = _uiState.value.copy(
                watchlistConfigTarget = null,
                testAlertSentMessage = "Added ${item.title} to Watchlist! Notification set for ${if (notifyDaysBefore == 0) "release day" else "$notifyDaysBefore day before"}."
            )
        }
    }

    fun removeFromWatchlist(mediaId: String) {
        viewModelScope.launch {
            watchlistRepository.removeFromWatchlist(mediaId)
            _uiState.value = _uiState.value.copy(testAlertSentMessage = "Removed from Watchlist")
        }
    }

    fun toggleWatchlistNotification(mediaId: String, currentEnabled: Boolean, daysBefore: Int) {
        viewModelScope.launch {
            watchlistRepository.updateNotificationSettings(mediaId, !currentEnabled, daysBefore)
        }
    }

    fun updateWatchlistStatus(mediaId: String, newStatus: WatchlistStatus) {
        viewModelScope.launch {
            watchlistRepository.updateStatus(mediaId, newStatus)
        }
    }

    fun updateWatchlistRatingAndNotes(mediaId: String, rating: Float, notes: String) {
        viewModelScope.launch {
            watchlistRepository.updateNotesAndRating(mediaId, rating, notes)
        }
    }

    fun sendTestAlert(title: String = "Avengers: Secret Wars") {
        watchlistRepository.triggerTestNotification(title)
        _uiState.value = _uiState.value.copy(
            testAlertSentMessage = "🔔 Automated notification sent for $title! Check your status bar."
        )
    }

    fun markAlertAsRead(alertId: Long) {
        viewModelScope.launch {
            watchlistRepository.markAlertAsRead(alertId)
        }
    }

    fun markAllAlertsAsRead() {
        viewModelScope.launch {
            watchlistRepository.markAllAlertsAsRead()
        }
    }

    fun deleteAlert(alertId: Long) {
        viewModelScope.launch {
            watchlistRepository.deleteAlert(alertId)
        }
    }

    fun clearAllAlerts() {
        viewModelScope.launch {
            watchlistRepository.clearAllAlerts()
        }
    }

    fun isMediaInWatchlist(mediaId: String): Boolean {
        return watchlistItems.value.any { it.mediaId == mediaId }
    }

    fun getWatchlistEntity(mediaId: String): WatchlistEntity? {
        return watchlistItems.value.find { it.mediaId == mediaId }
    }

    fun setGlobalAlerts(enabled: Boolean) {
        val app = getApplication<Application>()
        NotificationHelper.setGlobalAlertsEnabled(app, enabled)
        _isGlobalAlertsEnabled.value = enabled
        if (enabled) {
            val count = NotificationHelper.scheduleAllUpcomingReleases(app, MediaCatalog.items, _globalReminderLead.value)
            _armedReleasesCount.value = count
            _uiState.value = _uiState.value.copy(
                testAlertSentMessage = "Push notifications active for all upcoming movies & series! ($count armed)"
            )
        } else {
            NotificationHelper.cancelAllGlobalReminders(app, MediaCatalog.items)
            _armedReleasesCount.value = 0
            _uiState.value = _uiState.value.copy(
                testAlertSentMessage = "Global release push reminders disabled"
            )
        }
    }

    fun setGlobalReminderLead(lead: String) {
        val app = getApplication<Application>()
        NotificationHelper.setGlobalReminderLead(app, lead)
        _globalReminderLead.value = lead
        if (_isGlobalAlertsEnabled.value) {
            val count = NotificationHelper.scheduleAllUpcomingReleases(app, MediaCatalog.items, lead)
            _armedReleasesCount.value = count
            val leadLabel = NotificationHelper.getLeadLabel(lead)
            _uiState.value = _uiState.value.copy(
                testAlertSentMessage = "Release reminders updated: $leadLabel ($count armed)"
            )
        }
    }

    fun armAllUpcomingReleasesManually() {
        val app = getApplication<Application>()
        setGlobalAlerts(true)
        val count = NotificationHelper.scheduleAllUpcomingReleases(app, MediaCatalog.items, _globalReminderLead.value)
        _armedReleasesCount.value = count
        _uiState.value = _uiState.value.copy(
            testAlertSentMessage = "Armed push reminders for $count upcoming movies & series before release time!"
        )
    }

    fun getEffectiveTmdbKey(): String {
        val app = getApplication<Application>()
        val customKey = NotificationHelper.getTmdbApiKey(app)
        if (customKey.isNotBlank()) return customKey

        val buildConfigKey = com.example.BuildConfig.TMDB_API_KEY
        if (buildConfigKey.isNotBlank() && buildConfigKey != "YOUR_TMDB_API_KEY_HERE") {
            return buildConfigKey
        }
        return NotificationHelper.DEFAULT_TMDB_KEY
    }

    fun syncTmdbLive() {
        val apiKey = getEffectiveTmdbKey()
        viewModelScope.launch {
            val result = mediaRepository.fetchUpcomingFromTmdb(apiKey)
            result.onSuccess { count ->
                _uiState.value = _uiState.value.copy(
                    testAlertSentMessage = "🎬 Live TMDB Sync complete! $count upcoming releases updated."
                )
                if (_isGlobalAlertsEnabled.value) {
                    val armed = NotificationHelper.scheduleAllUpcomingReleases(
                        getApplication(),
                        allMedia.value,
                        _globalReminderLead.value
                    )
                    _armedReleasesCount.value = armed
                }
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    testAlertSentMessage = "TMDB Sync: ${err.message}"
                )
            }
        }
    }
}
