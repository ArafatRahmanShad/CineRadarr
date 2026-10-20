package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationAlertEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.MediaItem
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

    private const val PREFS_NAME = "cineradar_prefs"
    private const val KEY_GLOBAL_ALERTS_ENABLED = "key_global_alerts_enabled"
    private const val KEY_GLOBAL_REMINDER_LEAD = "key_global_reminder_lead"
    private const val KEY_TMDB_CUSTOM_KEY = "key_tmdb_custom_key"
    const val DEFAULT_TMDB_KEY = "e2d118be87f61f711a1f66295be7f0c2"

    // Lead options
    const val LEAD_2_HOURS_BEFORE = "2_HOURS"
    const val LEAD_1_DAY_BEFORE = "1_DAY"
    const val LEAD_2_DAYS_BEFORE = "2_DAYS"
    const val LEAD_1_WEEK_BEFORE = "1_WEEK"
    const val LEAD_RELEASE_MORNING = "RELEASE_DAY"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getTmdbApiKey(context: Context): String {
        return getPrefs(context).getString(KEY_TMDB_CUSTOM_KEY, DEFAULT_TMDB_KEY) ?: DEFAULT_TMDB_KEY
    }

    fun setTmdbApiKey(context: Context, key: String) {
        getPrefs(context).edit().putString(KEY_TMDB_CUSTOM_KEY, key.trim()).apply()
    }

    fun isGlobalAlertsEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_GLOBAL_ALERTS_ENABLED, true)
    }

    fun setGlobalAlertsEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_GLOBAL_ALERTS_ENABLED, enabled).apply()
    }

    fun getGlobalReminderLead(context: Context): String {
        return getPrefs(context).getString(KEY_GLOBAL_REMINDER_LEAD, LEAD_1_DAY_BEFORE) ?: LEAD_1_DAY_BEFORE
    }

    fun setGlobalReminderLead(context: Context, lead: String) {
        getPrefs(context).edit().putString(KEY_GLOBAL_REMINDER_LEAD, lead).apply()
    }

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun calculateLeadMillis(lead: String): Long {
        return when (lead) {
            LEAD_2_HOURS_BEFORE -> 2 * 3600000L
            LEAD_1_DAY_BEFORE -> 1 * 86400000L
            LEAD_2_DAYS_BEFORE -> 2 * 86400000L
            LEAD_1_WEEK_BEFORE -> 7 * 86400000L
            LEAD_RELEASE_MORNING -> 0L
            else -> 86400000L
        }
    }

    fun getLeadLabel(lead: String): String {
        return when (lead) {
            LEAD_2_HOURS_BEFORE -> "2 Hours Before"
            LEAD_1_DAY_BEFORE -> "1 Day Before"
            LEAD_2_DAYS_BEFORE -> "2 Days Before"
            LEAD_1_WEEK_BEFORE -> "1 Week Before"
            LEAD_RELEASE_MORNING -> "On Release Morning"
            else -> "1 Day Before"
        }
    }

    fun scheduleMediaItemReminder(context: Context, item: MediaItem, lead: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val leadMillis = calculateLeadMillis(lead)
        val triggerTime = item.releaseDateEpoch - leadMillis
        val now = System.currentTimeMillis()

        if (triggerTime <= now) return

        val intent = Intent(context, ReleaseNotificationReceiver::class.java).apply {
            action = ACTION_RELEASE_ALERT
            putExtra(EXTRA_MEDIA_ID, item.id)
            putExtra(EXTRA_MEDIA_TITLE, item.title)
            val timingText = when (lead) {
                LEAD_2_HOURS_BEFORE -> "premieres in 2 hours on ${item.platform}!"
                LEAD_1_DAY_BEFORE -> "premieres tomorrow on ${item.platform}!"
                LEAD_2_DAYS_BEFORE -> "premieres in 2 days on ${item.platform}!"
                LEAD_1_WEEK_BEFORE -> "premieres in 1 week on ${item.platform}!"
                LEAD_RELEASE_MORNING -> "premieres TODAY on ${item.platform}! Don't miss it."
                else -> "premieres soon on ${item.platform}!"
            }
            putExtra(EXTRA_MESSAGE, "🎬 ${item.title} $timingText")
            putExtra(EXTRA_ALERT_TYPE, if (lead == LEAD_RELEASE_MORNING) "RELEASE_DAY" else "REMINDER")
        }

        val requestCode = ("global_" + item.id).hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelMediaItemReminder(context: Context, mediaId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReleaseNotificationReceiver::class.java).apply {
            action = ACTION_RELEASE_ALERT
        }
        val requestCode = ("global_" + mediaId).hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun scheduleAllUpcomingReleases(context: Context, items: List<MediaItem>, lead: String): Int {
        var count = 0
        val now = System.currentTimeMillis()
        val leadMillis = calculateLeadMillis(lead)

        items.forEach { item ->
            val triggerTime = item.releaseDateEpoch - leadMillis
            if (triggerTime > now) {
                scheduleMediaItemReminder(context, item, lead)
                count++
            }
        }
        return count
    }

    fun cancelAllGlobalReminders(context: Context, items: List<MediaItem>) {
        items.forEach { item ->
            cancelMediaItemReminder(context, item.id)
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
            } catch (_: SecurityException) {
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
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
        }
    }

    fun sendTestNotification(context: Context, mediaTitle: String = "Avengers: Secret Wars") {
        val notifId = (System.currentTimeMillis() % 100000).toInt()
        val title = "⚡ CineRadar Release Reminder"
        val message = "Upcoming Alert: $mediaTitle is scheduled to premiere soon! Trailer & schedule updated."

        showSystemNotification(context, notifId, title, message)

        // Record in local database alert history
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.getInstance(context).alertDao().insertAlert(
                    NotificationAlertEntity(
                        mediaId = "test_alert_$notifId",
                        mediaTitle = mediaTitle,
                        alertType = "REMINDER",
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
