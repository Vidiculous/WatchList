package com.watchlist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.watchlist.data.db.entity.WatchlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistItemDao {

    @Query("SELECT * FROM watchlist_items ORDER BY addedAt DESC")
    fun getAll(): Flow<List<WatchlistItemEntity>>

    @Query("SELECT * FROM watchlist_items WHERE tmdbId = :tmdbId AND type = :type LIMIT 1")
    suspend fun findByTmdbId(tmdbId: Int, type: String): WatchlistItemEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: WatchlistItemEntity): Long

    @Query("DELETE FROM watchlist_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE watchlist_items SET voteAverage = :voteAverage WHERE id = :id")
    suspend fun updateVoteAverage(id: Long, voteAverage: Double)

    @Query("UPDATE watchlist_items SET releaseDate = :releaseDate WHERE id = :id")
    suspend fun updateReleaseDate(id: Long, releaseDate: String)
}
