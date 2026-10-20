package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedEpoch DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist WHERE status = :status ORDER BY addedEpoch DESC")
    fun getWatchlistByStatus(status: String): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getWatchlistItem(mediaId: String): WatchlistEntity?

    @Query("SELECT * FROM watchlist WHERE mediaId = :mediaId LIMIT 1")
    fun observeWatchlistItem(mediaId: String): Flow<WatchlistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: WatchlistEntity)

    @Update
    suspend fun update(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE mediaId = :mediaId")
    suspend fun deleteById(mediaId: String)

    @Query("UPDATE watchlist SET notifyEnabled = :enabled, notifyDaysBefore = :daysBefore WHERE mediaId = :mediaId")
    suspend fun updateNotificationSettings(mediaId: String, enabled: Boolean, daysBefore: Int)

    @Query("UPDATE watchlist SET status = :newStatus WHERE mediaId = :mediaId")
    suspend fun updateStatus(mediaId: String, newStatus: String)

    @Query("UPDATE watchlist SET personalRating = :rating, personalNotes = :notes WHERE mediaId = :mediaId")
    suspend fun updateNotesAndRating(mediaId: String, rating: Float, notes: String)
}
