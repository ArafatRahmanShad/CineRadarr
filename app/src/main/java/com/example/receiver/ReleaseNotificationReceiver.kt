package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationAlertEntity
import com.example.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReleaseNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == NotificationHelper.ACTION_RELEASE_ALERT) {
            val mediaId = intent.getStringExtra(NotificationHelper.EXTRA_MEDIA_ID) ?: ""
            val mediaTitle = intent.getStringExtra(NotificationHelper.EXTRA_MEDIA_TITLE) ?: "Upcoming Movie / Series"
            val message = intent.getStringExtra(NotificationHelper.EXTRA_MESSAGE) ?: "$mediaTitle is premiering!"
            val alertType = intent.getStringExtra(NotificationHelper.EXTRA_ALERT_TYPE) ?: "RELEASE"

            val notifId = mediaId.hashCode()
            val title = "🎬 CineRadar Premiere: $mediaTitle"
            NotificationHelper.showSystemNotification(context, notifId, title, message, mediaId)

            // Record into alerts database
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    db.alertDao().insertAlert(
                        NotificationAlertEntity(
                            mediaId = mediaId,
                            mediaTitle = mediaTitle,
                            alertType = alertType,
                            alertMessage = message,
                            scheduledEpoch = System.currentTimeMillis(),
                            deliveredEpoch = System.currentTimeMillis(),
                            isDelivered = true,
                            isRead = false
                        )
                    )
                } catch (_: Exception) {
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
