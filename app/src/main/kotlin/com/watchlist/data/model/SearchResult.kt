package com.watchlist.data.model

data class SearchResult(
    val tmdbId: Int,
    val title: String,
    val type: MediaType,
    val posterPath: String?,
    val voteAverage: Double?,
    val releaseDate: String?
)
