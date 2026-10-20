package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val mediaId: String,
    val title: String,
    val mediaType: String, // MOVIE or SERIES
    val releaseDateEpoch: Long,
    val releaseDateDisplay: String,
    val platform: String,
    val posterUrl: String,
    val genres: String, // Comma-delimited
    val status: String = "WANT_TO_WATCH", // WANT_TO_WATCH, WATCHING, WATCHED
    val notifyEnabled: Boolean = true,
    val notifyDaysBefore: Int = 1, // 0 = day of release, 1 = 1 day before, 7 = 1 week before
    val personalRating: Float = 0f,
    val personalNotes: String = "",
    val addedEpoch: Long = System.currentTimeMillis()
)
