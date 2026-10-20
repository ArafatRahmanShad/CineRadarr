package com.example.data.model

enum class MediaType(val label: String) {
    MOVIE("Movie"),
    SERIES("TV Series")
}

enum class ReleaseStatus(val label: String) {
    UPCOMING("Upcoming"),
    PREMIERING_SOON("Premiere Soon"),
    IN_THEATERS("In Theaters"),
    STREAMING_NOW("Streaming")
}

enum class WatchlistStatus(val label: String) {
    WANT_TO_WATCH("Want to Watch"),
    WATCHING("Watching"),
    WATCHED("Watched")
}

data class MediaUpdate(
    val id: String,
    val mediaId: String,
    val mediaTitle: String,
    val updateType: String, // e.g. "Official Trailer", "Release Date", "Sneak Peek", "Casting"
    val headline: String,
    val summary: String,
    val timestampDaysAgo: Int,
    val isHot: Boolean = false
)

data class MediaItem(
    val id: String,
    val title: String,
    val type: MediaType,
    val releaseDateEpoch: Long, // Epoch millis
    val releaseDateDisplay: String, // e.g. "Nov 6, 2026"
    val status: ReleaseStatus,
    val posterUrl: String,
    val backdropUrl: String,
    val platform: String, // e.g. "Theaters & IMAX", "Netflix", "HBO Max", "Disney+", "Apple TV+"
    val genres: List<String>,
    val hypeScore: Int, // 0 - 100%
    val rating: Double, // e.g. 8.7
    val synopsis: String,
    val directorOrCreator: String,
    val cast: List<String>,
    val trailerQuery: String,
    val seasons: Int? = null,
    val episodes: Int? = null,
    val latestUpdate: MediaUpdate? = null
) {
    fun getDaysUntilRelease(currentEpoch: Long = System.currentTimeMillis()): Long {
        val diffMillis = releaseDateEpoch - currentEpoch
        return if (diffMillis <= 0) 0 else (diffMillis / (1000 * 60 * 60 * 24))
    }

    fun getReleaseCountdownString(currentEpoch: Long = System.currentTimeMillis()): String {
        val days = getDaysUntilRelease(currentEpoch)
        return when {
            days == 0L -> "Premiering Today!"
            days == 1L -> "Tomorrow!"
            days < 7L -> "In $days days"
            days < 30L -> "In ${(days / 7)} weeks"
            days < 365L -> "In ${(days / 30)} months"
            else -> "In ${(days / 365)} year+"
        }
    }

    fun generateTelegramShareText(): String {
        val typeStr = if (type == MediaType.MOVIE) "🎬 Movie" else "📺 Series"
        return """
🍿 $title ($typeStr)
📅 Release: $releaseDateDisplay (${getReleaseCountdownString()})
⚡ Platform: $platform
🔥 Hype Score: $hypeScore% | ⭐ $rating/10
🎭 Genres: ${genres.joinToString(", ")}

📝 Synopsis:
$synopsis

🚨 Latest Update: ${latestUpdate?.headline ?: "Release confirmed"}

Tracked via CineRadar Radar app!
        """.trimIndent()
    }
}
