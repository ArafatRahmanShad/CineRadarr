package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationAlertEntity
import com.example.data.local.WatchlistEntity
import com.example.receiver.ReleaseNotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationHelper {
    const val CHANNEL_ID = "cineradar_release_alerts"
    const val CHANNEL_NAME = "Movie & Series Release Alerts"
    const val CHANNEL_DESC = "Notifications for upcoming movie and series releases and watchlist reminders"

    const val ACTION_RELEASE_ALERT = "com.example.ACTION_RELEASE_ALERT"
    const val EXTRA_MEDIA_ID = "extra_media_id"
    const val EXTRA_MEDIA_TITLE = "extra_media_title"
    const val EXTRA_MESSAGE = "extra_message"
    const val EXTRA_ALERT_TYPE = "extra_alert_type"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReleaseNotification(context: Context, item: WatchlistEntity) {
        if (!item.notifyEnabled) {
            cancelReleaseNotification(context, item.mediaId)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReleaseNotificationReceiver::class.java).apply {
            action = ACTION_RELEASE_ALERT
            putExtra(EXTRA_MEDIA_ID, item.mediaId)
            putExtra(EXTRA_MEDIA_TITLE, item.title)
            val timingMsg = when (item.notifyDaysBefore) {
                0 -> "releases TODAY! Check out platforms and showtimes."
                1 -> "releases TOMORROW! Get ready to watch on ${item.platform}."
                7 -> "releases in 1 week on ${item.platform}!"
                else -> "releases soon on ${item.platform}!"
            }
            putExtra(EXTRA_MESSAGE, "${item.title} $timingMsg")
            putExtra(EXTRA_ALERT_TYPE, if (item.notifyDaysBefore == 0) "RELEASE_DAY" else "REMINDER")
        }

        val requestCode = item.mediaId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = item.releaseDateEpoch - (item.notifyDaysBefore * 86400000L)
        val now = System.currentTimeMillis()

        if (triggerTime > now) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } catch (e: SecurityException) {
                // In case exact alarm permission is restricted on Android 12+, fallback to inexact
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        }
    }

    fun cancelReleaseNotification(context: Context, mediaId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReleaseNotificationReceiver::class.java).apply {
            action = ACTION_RELEASE_ALERT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            mediaId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun showSystemNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        mediaId: String? = null
    ) {
        initNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (mediaId != null) {
                putExtra(EXTRA_MEDIA_ID, mediaId)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Android 13+ POST_NOTIFICATIONS may not be granted yet
        }
    }

    fun sendTestNotification(context: Context, mediaTitle: String = "Avengers: Secret Wars") {
        val notifId = (System.currentTimeMillis() % 100000).toInt()
        val title = "⚡ CineRadar Release Alert"
        val message = "Upcoming Alert: $mediaTitle has dropped a new trailer and is set for premiering soon!"
        
        showSystemNotification(context, notifId, title, message)

        // Record in local database alert history
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.getInstance(context).alertDao().insertAlert(
                    NotificationAlertEntity(
                        mediaId = "test_alert_$notifId",
                        mediaTitle = mediaTitle,
                        alertType = "TRAILER_DROP",
                        alertMessage = message,
                        scheduledEpoch = System.currentTimeMillis(),
                        deliveredEpoch = System.currentTimeMillis(),
                        isDelivered = true,
                        isRead = false
                    )
                )
            } catch (_: Exception) {}
        }
    }
}
