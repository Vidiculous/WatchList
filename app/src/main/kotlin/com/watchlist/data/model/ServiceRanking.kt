package com.watchlist.data.model

data class ServiceRanking(
    val serviceName: String,
    val logoPath: String?,
    val availableItems: List<WatchlistItem>,
    val count: Int = availableItems.size
)
