package com.example.data.remote

import com.squareup.moshi.Json

data class TmdbPaginatedResponse<T>(
    @field:Json(name = "page") val page: Int = 1,
    @field:Json(name = "results") val results: List<T> = emptyList(),
    @field:Json(name = "total_pages") val totalPages: Int = 1,
    @field:Json(name = "total_results") val totalResults: Int = 0
)

data class TmdbMovieDto(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "overview") val overview: String? = null,
    @field:Json(name = "release_date") val releaseDate: String? = null,
    @field:Json(name = "poster_path") val posterPath: String? = null,
    @field:Json(name = "backdrop_path") val backdropPath: String? = null,
    @field:Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @field:Json(name = "vote_count") val voteCount: Int? = 0,
    @field:Json(name = "genre_ids") val genreIds: List<Int>? = emptyList(),
    @field:Json(name = "popularity") val popularity: Double? = 0.0
)

data class TmdbTvDto(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "overview") val overview: String? = null,
    @field:Json(name = "first_air_date") val firstAirDate: String? = null,
    @field:Json(name = "poster_path") val posterPath: String? = null,
    @field:Json(name = "backdrop_path") val backdropPath: String? = null,
    @field:Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @field:Json(name = "genre_ids") val genreIds: List<Int>? = emptyList(),
    @field:Json(name = "popularity") val popularity: Double? = 0.0
)
