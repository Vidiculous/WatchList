package com.watchlist.data.model

data class StreamingAvailability(
    val id: Long = 0,
    val watchlistItemId: Long,
    val serviceName: String,
    val serviceLogoPath: String?,
    val countryCode: String,
    val fetchedAt: Long = System.currentTimeMillis()
)
