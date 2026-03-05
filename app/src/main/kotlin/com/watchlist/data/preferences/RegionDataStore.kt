package com.watchlist.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "watchlist_prefs")

@Singleton
class RegionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val regionKey = stringPreferencesKey("region_code")
    private val apiKeyKey = stringPreferencesKey("tmdb_api_key")

    val regionCode: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[regionKey] ?: "US" }

    val apiKey: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[apiKeyKey] ?: "" }

    suspend fun setRegion(code: String) {
        context.dataStore.edit { it[regionKey] = code }
    }

    suspend fun setApiKey(key: String) {
        context.dataStore.edit { it[apiKeyKey] = key.trim() }
    }
}
