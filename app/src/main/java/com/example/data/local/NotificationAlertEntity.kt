package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_alerts")
data class NotificationAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mediaId: String,
    val mediaTitle: String,
    val alertType: String, // "RELEASE_DAY", "REMINDER", "TRAILER", "UPDATE"
    val alertMessage: String,
    val scheduledEpoch: Long,
    val deliveredEpoch: Long = 0L,
    val isDelivered: Boolean = false,
    val isRead: Boolean = false
)
