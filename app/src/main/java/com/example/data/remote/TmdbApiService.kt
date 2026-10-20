package com.example.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Header("Authorization") authHeader: String? = null,
        @Query("api_key") apiKey: String? = null,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "US"
    ): TmdbPaginatedResponse<TmdbMovieDto>

    @GET("tv/on_the_air")
    suspend fun getUpcomingSeries(
        @Header("Authorization") authHeader: String? = null,
        @Query("api_key") apiKey: String? = null,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): TmdbPaginatedResponse<TmdbTvDto>

    @GET("trending/all/week")
    suspend fun getTrendingAll(
        @Header("Authorization") authHeader: String? = null,
        @Query("api_key") apiKey: String? = null,
        @Query("page") page: Int = 1
    ): TmdbPaginatedResponse<TmdbMovieDto>
}
