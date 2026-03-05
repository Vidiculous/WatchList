package com.watchlist.data.preferences

import com.watchlist.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds the active TMDB API key at runtime.
 * Populated by WatchListApp on startup from DataStore.
 * Falls back to the build-time key from local.properties if nothing is stored.
 */
@Singleton
class ApiKeyProvider @Inject constructor() {
    @Volatile var key: String = BuildConfig.TMDB_READ_ACCESS_TOKEN
}
