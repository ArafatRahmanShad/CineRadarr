package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM notification_alerts ORDER BY id DESC")
    fun getAllAlerts(): Flow<List<NotificationAlertEntity>>

    @Query("SELECT COUNT(*) FROM notification_alerts WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: NotificationAlertEntity): Long

    @Query("UPDATE notification_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notification_alerts SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notification_alerts WHERE id = :id")
    suspend fun deleteAlert(id: Long)

    @Query("DELETE FROM notification_alerts")
    suspend fun clearAllAlerts()
}
