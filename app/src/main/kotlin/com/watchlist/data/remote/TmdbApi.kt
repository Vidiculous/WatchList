package com.watchlist.data.remote

import com.watchlist.data.remote.dto.TmdbSearchResponse
import com.watchlist.data.remote.dto.TmdbVoteDto
import com.watchlist.data.remote.dto.TmdbWatchProvidersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TmdbSearchResponse

    @GET("3/search/tv")
    suspend fun searchTv(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TmdbSearchResponse

    @GET("3/movie/{id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("id") tmdbId: Int
    ): TmdbWatchProvidersResponse

    @GET("3/tv/{id}/watch/providers")
    suspend fun getTvWatchProviders(
        @Path("id") tmdbId: Int
    ): TmdbWatchProvidersResponse

    @GET("3/movie/{id}")
    suspend fun getMovieDetails(@Path("id") id: Int): TmdbVoteDto

    @GET("3/tv/{id}")
    suspend fun getTvDetails(@Path("id") id: Int): TmdbVoteDto

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342"
        const val LOGO_BASE_URL = "https://image.tmdb.org/t/p/w92"
    }
}
