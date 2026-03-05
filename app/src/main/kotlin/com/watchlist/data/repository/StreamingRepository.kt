package com.watchlist.data.repository

import com.watchlist.data.db.dao.StreamingAvailabilityDao
import com.watchlist.data.db.entity.StreamingAvailabilityEntity
import com.watchlist.data.db.entity.toDomain
import com.watchlist.data.model.MediaType
import com.watchlist.data.model.StreamingAvailability
import com.watchlist.data.model.WatchlistItem
import com.watchlist.data.remote.TmdbApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingRepository @Inject constructor(
    private val api: TmdbApi,
    private val dao: StreamingAvailabilityDao
) {
    fun getAvailabilityForCountry(countryCode: String): Flow<List<StreamingAvailability>> =
        dao.getByCountry(countryCode).map { list -> list.map { it.toDomain() } }

    suspend fun refreshAll(items: List<WatchlistItem>, countryCode: String) {
        dao.deleteAll()
        val newRows = mutableListOf<StreamingAvailabilityEntity>()
        for (item in items) {
            try {
                val response = when (item.type) {
                    MediaType.MOVIE -> api.getMovieWatchProviders(item.tmdbId)
                    MediaType.TV -> api.getTvWatchProviders(item.tmdbId)
                }
                val providers = response.results[countryCode]
                val flatrateProviders = providers?.flatrate ?: emptyList()
                flatrateProviders.forEach { provider ->
                    newRows += StreamingAvailabilityEntity(
                        watchlistItemId = item.id,
                        serviceName = provider.providerName,
                        serviceLogoPath = provider.logoPath,
                        countryCode = countryCode,
                        fetchedAt = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                // Skip item on error — partial refresh is acceptable
            }
            delay(150) // Rate-limit mitigation: TMDB allows 40 requests per 10 seconds
        }
        dao.insertAll(newRows)
    }
}
