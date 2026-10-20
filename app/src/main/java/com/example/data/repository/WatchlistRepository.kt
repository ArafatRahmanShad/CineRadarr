package com.example.data.repository

import android.content.Context
import com.example.data.local.AlertDao
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationAlertEntity
import com.example.data.local.WatchlistDao
import com.example.data.local.WatchlistEntity
import com.example.data.model.MediaItem
import com.example.data.model.WatchlistStatus
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow

class WatchlistRepository(
    private val context: Context,
    private val watchlistDao: WatchlistDao,
    private val alertDao: AlertDao
) {
    companion object {
        @Volatile
        private var INSTANCE: WatchlistRepository? = null

        fun getInstance(context: Context): WatchlistRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val instance = WatchlistRepository(context.applicationContext, db.watchlistDao(), db.alertDao())
                INSTANCE = instance
                instance
            }
        }
    }

    val watchlist: Flow<List<WatchlistEntity>> = watchlistDao.getAllWatchlist()
    val alerts: Flow<List<NotificationAlertEntity>> = alertDao.getAllAlerts()
    val unreadAlertCount: Flow<Int> = alertDao.getUnreadCount()

    fun observeWatchlistItem(mediaId: String): Flow<WatchlistEntity?> {
        return watchlistDao.observeWatchlistItem(mediaId)
    }

    suspend fun getWatchlistItem(mediaId: String): WatchlistEntity? {
        return watchlistDao.getWatchlistItem(mediaId)
    }

    suspend fun addToWatchlist(
        item: MediaItem,
        status: WatchlistStatus = WatchlistStatus.WANT_TO_WATCH,
        notifyEnabled: Boolean = true,
        notifyDaysBefore: Int = 1,
        notes: String = ""
    ) {
        val entity = WatchlistEntity(
            mediaId = item.id,
            title = item.title,
            mediaType = item.type.name,
            releaseDateEpoch = item.releaseDateEpoch,
            releaseDateDisplay = item.releaseDateDisplay,
            platform = item.platform,
            posterUrl = item.posterUrl,
            genres = item.genres.joinToString(","),
            status = status.name,
            notifyEnabled = notifyEnabled,
            notifyDaysBefore = notifyDaysBefore,
            personalNotes = notes,
            addedEpoch = System.currentTimeMillis()
        )
        watchlistDao.insertOrUpdate(entity)

        if (notifyEnabled) {
            NotificationHelper.scheduleReleaseNotification(context, entity)
        } else {
            NotificationHelper.cancelReleaseNotification(context, item.id)
        }
    }

    suspend fun removeFromWatchlist(mediaId: String) {
        watchlistDao.deleteById(mediaId)
        NotificationHelper.cancelReleaseNotification(context, mediaId)
    }

    suspend fun updateNotificationSettings(mediaId: String, enabled: Boolean, daysBefore: Int) {
        watchlistDao.updateNotificationSettings(mediaId, enabled, daysBefore)
        val item = watchlistDao.getWatchlistItem(mediaId)
        if (item != null) {
            if (enabled) {
                NotificationHelper.scheduleReleaseNotification(context, item.copy(notifyEnabled = enabled, notifyDaysBefore = daysBefore))
            } else {
                NotificationHelper.cancelReleaseNotification(context, mediaId)
            }
        }
    }

    suspend fun updateStatus(mediaId: String, status: WatchlistStatus) {
        watchlistDao.updateStatus(mediaId, status.name)
    }

    suspend fun updateNotesAndRating(mediaId: String, rating: Float, notes: String) {
        watchlistDao.updateNotesAndRating(mediaId, rating, notes)
    }

    suspend fun markAlertAsRead(alertId: Long) {
        alertDao.markAsRead(alertId)
    }

    suspend fun markAllAlertsAsRead() {
        alertDao.markAllAsRead()
    }

    suspend fun deleteAlert(alertId: Long) {
        alertDao.deleteAlert(alertId)
    }

    suspend fun clearAllAlerts() {
        alertDao.clearAllAlerts()
    }

    fun triggerTestNotification(mediaTitle: String = "Avengers: Secret Wars") {
        NotificationHelper.sendTestNotification(context, mediaTitle)
    }
}
