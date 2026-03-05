package com.watchlist.data.model

data class WatchlistItem(
    val id: Long = 0,
    val tmdbId: Int,
    val title: String,
    val type: MediaType,
    val posterPath: String?,
    val addedAt: Long = System.currentTimeMillis()
)
