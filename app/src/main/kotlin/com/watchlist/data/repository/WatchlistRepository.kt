package com.watchlist.data.repository

import com.watchlist.data.db.dao.WatchlistItemDao
import com.watchlist.data.db.entity.toDomain
import com.watchlist.data.db.entity.toEntity
import com.watchlist.data.model.MediaType
import com.watchlist.data.model.WatchlistItem
import com.watchlist.data.remote.TmdbApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
    private val dao: WatchlistItemDao,
    private val api: TmdbApi
) {
    val watchlist: Flow<List<WatchlistItem>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    val unwatchedItems: Flow<List<WatchlistItem>> =
        dao.getAllUnwatched().map { list -> list.map { it.toDomain() } }

    val watchedItems: Flow<List<WatchlistItem>> =
        dao.getAllWatched().map { list -> list.map { it.toDomain() } }

    suspend fun addItem(item: WatchlistItem): Long = dao.insert(item.toEntity())

    suspend fun removeItem(id: Long) = dao.deleteById(id)

    suspend fun markWatched(id: Long, watched: Boolean) = dao.updateWatched(id, watched)

    suspend fun setUserRating(id: Long, rating: Int?) = dao.updateUserRating(id, rating)

    suspend fun isAlreadyAdded(tmdbId: Int, type: MediaType): Boolean =
        dao.findByTmdbId(tmdbId, type.name) != null

    suspend fun refreshMissingMetadata(items: List<WatchlistItem>) {
        for (item in items) {
            if (item.voteAverage != null && item.releaseDate != null) continue
            try {
                val dto = when (item.type) {
                    MediaType.MOVIE -> api.getMovieDetails(item.tmdbId)
                    MediaType.TV -> api.getTvDetails(item.tmdbId)
                }
                if (item.voteAverage == null) {
                    dto.voteAverage?.takeIf { it > 0 }?.let { dao.updateVoteAverage(item.id, it) }
                }
                if (item.releaseDate == null) {
                    val date = dto.releaseDate?.takeIf { it.isNotBlank() }
                        ?: dto.firstAirDate?.takeIf { it.isNotBlank() }
                    date?.let { dao.updateReleaseDate(item.id, it) }
                }
            } catch (_: Exception) { }
        }
    }
}
