package com.watchlist

import android.app.Application
import com.watchlist.data.preferences.ApiKeyProvider
import com.watchlist.data.preferences.RegionDataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class WatchListApp : Application() {

    @Inject lateinit var regionDataStore: RegionDataStore
    @Inject lateinit var apiKeyProvider: ApiKeyProvider

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        // Synchronous initial read — ensures key is set before any API call fires
        val initialKey = runBlocking(Dispatchers.IO) { regionDataStore.apiKey.first() }
        apiKeyProvider.key = initialKey.ifBlank { BuildConfig.TMDB_READ_ACCESS_TOKEN }
        // Keep collecting for live updates when user changes key in Settings
        scope.launch {
            regionDataStore.apiKey.collect { storedKey ->
                apiKeyProvider.key = storedKey.ifBlank { BuildConfig.TMDB_READ_ACCESS_TOKEN }
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        scope.cancel()
    }
}
