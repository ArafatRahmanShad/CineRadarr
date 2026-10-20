package com.example.data.remote

import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.MediaUpdate
import com.example.data.model.ReleaseStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object TmdbNetworkClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w780"
    const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val apiService: TmdbApiService by lazy {
        retrofit.create(TmdbApiService::class.java)
    }

    fun mapGenreIdsToNames(genreIds: List<Int>?): List<String> {
        if (genreIds.isNullOrEmpty()) return listOf("Drama")
        return genreIds.mapNotNull { id ->
            when (id) {
                28 -> "Action"
                12 -> "Adventure"
                16 -> "Animation"
                35 -> "Comedy"
                80 -> "Crime"
                99 -> "Documentary"
                18 -> "Drama"
                10751 -> "Family"
                14 -> "Fantasy"
                36 -> "History"
                27 -> "Horror"
                10402 -> "Music"
                9648 -> "Mystery"
                10749 -> "Romance"
                878 -> "Sci-Fi"
                10770 -> "TV Movie"
                53 -> "Thriller"
                10752 -> "War"
                37 -> "Western"
                10759 -> "Action & Adventure"
                10762 -> "Kids"
                10763 -> "News"
                10764 -> "Reality"
                10765 -> "Sci-Fi & Fantasy"
                10766 -> "Soap"
                10767 -> "Talk"
                10768 -> "War & Politics"
                else -> null
            }
        }.ifEmpty { listOf("Cinema") }
    }

    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val outputDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    fun parseDateToEpoch(dateStr: String?): Long {
        if (dateStr.isNullOrBlank()) return System.currentTimeMillis() + 86400000L * 30
        return try {
            inputDateFormat.parse(dateStr)?.time ?: (System.currentTimeMillis() + 86400000L * 30)
        } catch (_: Exception) {
            System.currentTimeMillis() + 86400000L * 30
        }
    }

    fun formatDateDisplay(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return "Coming Soon"
        return try {
            val date = inputDateFormat.parse(dateStr)
            if (date != null) outputDateFormat.format(date) else "Coming Soon"
        } catch (_: Exception) {
            "Coming Soon"
        }
    }

    fun mapMovieToMediaItem(dto: TmdbMovieDto): MediaItem {
        val releaseEpoch = parseDateToEpoch(dto.releaseDate)
        val now = System.currentTimeMillis()
        val daysUntil = (releaseEpoch - now) / 86400000L

        val status = when {
            daysUntil <= 0 -> ReleaseStatus.IN_THEATERS
            daysUntil <= 21 -> ReleaseStatus.PREMIERING_SOON
            else -> ReleaseStatus.UPCOMING
        }

        val poster = if (!dto.posterPath.isNullOrBlank()) {
            "$POSTER_BASE_URL${dto.posterPath}"
        } else {
            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80"
        }

        val backdrop = if (!dto.backdropPath.isNullOrBlank()) {
            "$IMAGE_BASE_URL${dto.backdropPath}"
        } else {
            poster
        }

        val hype = ((dto.voteAverage ?: 7.0) * 10).toInt().coerceIn(60, 99)

        return MediaItem(
            id = "tmdb_movie_${dto.id}",
            title = dto.title,
            type = MediaType.MOVIE,
            releaseDateEpoch = releaseEpoch,
            releaseDateDisplay = formatDateDisplay(dto.releaseDate),
            status = status,
            posterUrl = poster,
            backdropUrl = backdrop,
            platform = "Theaters & Digital",
            genres = mapGenreIdsToNames(dto.genreIds),
            hypeScore = hype,
            rating = String.format(Locale.US, "%.1f", dto.voteAverage ?: 7.5).toDoubleOrNull() ?: 7.5,
            synopsis = dto.overview.takeIf { !it.isNullOrBlank() } ?: "Upcoming theatrical and digital release. Full details and trailer dropping soon.",
            directorOrCreator = "TMDB Featured Director",
            cast = listOf("Ensemble Cast", "All-Star Production"),
            trailerQuery = "${dto.title} official trailer",
            latestUpdate = MediaUpdate(
                id = "up_tmdb_m_${dto.id}",
                mediaId = "tmdb_movie_${dto.id}",
                mediaTitle = dto.title,
                updateType = "TMDB Sync",
                headline = "Official Release Date & Metadata Updated",
                summary = "Latest verified dates and synopses synced live from The Movie Database.",
                timestampDaysAgo = 0,
                isHot = (dto.popularity ?: 0.0) > 100.0
            )
        )
    }

    fun mapTvToMediaItem(dto: TmdbTvDto): MediaItem {
        val releaseEpoch = parseDateToEpoch(dto.firstAirDate)
        val now = System.currentTimeMillis()
        val daysUntil = (releaseEpoch - now) / 86400000L

        val status = when {
            daysUntil <= 0 -> ReleaseStatus.STREAMING_NOW
            daysUntil <= 21 -> ReleaseStatus.PREMIERING_SOON
            else -> ReleaseStatus.UPCOMING
        }

        val poster = if (!dto.posterPath.isNullOrBlank()) {
            "$POSTER_BASE_URL${dto.posterPath}"
        } else {
            "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=600&auto=format&fit=crop&q=80"
        }

        val backdrop = if (!dto.backdropPath.isNullOrBlank()) {
            "$IMAGE_BASE_URL${dto.backdropPath}"
        } else {
            poster
        }

        val hype = ((dto.voteAverage ?: 7.2) * 10).toInt().coerceIn(60, 99)

        return MediaItem(
            id = "tmdb_tv_${dto.id}",
            title = dto.name,
            type = MediaType.SERIES,
            releaseDateEpoch = releaseEpoch,
            releaseDateDisplay = formatDateDisplay(dto.firstAirDate),
            status = status,
            posterUrl = poster,
            backdropUrl = backdrop,
            platform = "Streaming & Networks",
            genres = mapGenreIdsToNames(dto.genreIds),
            hypeScore = hype,
            rating = String.format(Locale.US, "%.1f", dto.voteAverage ?: 7.8).toDoubleOrNull() ?: 7.8,
            synopsis = dto.overview.takeIf { !it.isNullOrBlank() } ?: "Upcoming television series and streaming release. Episodes schedule confirmed.",
            directorOrCreator = "Showrunners & Creators",
            cast = listOf("Starring Lead Cast"),
            trailerQuery = "${dto.name} official series trailer",
            seasons = 1,
            latestUpdate = MediaUpdate(
                id = "up_tmdb_t_${dto.id}",
                mediaId = "tmdb_tv_${dto.id}",
                mediaTitle = dto.name,
                updateType = "Air Date Confirmed",
                headline = "Episode Schedule & Poster Released",
                summary = "Live series updates and season premiere details synced from TMDB.",
                timestampDaysAgo = 0,
                isHot = (dto.popularity ?: 0.0) > 80.0
            )
        )
    }
}
