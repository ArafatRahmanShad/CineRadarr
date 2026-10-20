package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TmdbPaginatedResponse<T>(
    @Json(name = "page") val page: Int = 1,
    @Json(name = "results") val results: List<T> = emptyList(),
    @Json(name = "total_pages") val totalPages: Int = 1,
    @Json(name = "total_results") val totalResults: Int = 0
)

@JsonClass(generateAdapter = true)
data class TmdbMovieDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @Json(name = "vote_count") val voteCount: Int? = 0,
    @Json(name = "genre_ids") val genreIds: List<Int>? = emptyList(),
    @Json(name = "popularity") val popularity: Double? = 0.0
)

@JsonClass(generateAdapter = true)
data class TmdbTvDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @Json(name = "genre_ids") val genreIds: List<Int>? = emptyList(),
    @Json(name = "popularity") val popularity: Double? = 0.0
)
