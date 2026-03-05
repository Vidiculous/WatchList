package com.watchlist.data.repository

import com.watchlist.data.db.dao.WatchlistItemDao
import com.watchlist.data.db.entity.toDomain
import com.watchlist.data.db.entity.toEntity
import com.watchlist.data.model.MediaType
import com.watchlist.data.model.WatchlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
    private val dao: WatchlistItemDao
) {
    val watchlist: Flow<List<WatchlistItem>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun addItem(item: WatchlistItem): Long = dao.insert(item.toEntity())

    suspend fun removeItem(id: Long) = dao.deleteById(id)

    suspend fun isAlreadyAdded(tmdbId: Int, type: MediaType): Boolean =
        dao.findByTmdbId(tmdbId, type.name) != null
}
