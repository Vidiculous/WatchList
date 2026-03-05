package com.watchlist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.watchlist.data.model.MediaType
import com.watchlist.data.model.WatchlistItem

@Entity(tableName = "watchlist_items")
data class WatchlistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tmdbId: Int,
    val title: String,
    val type: String,
    val posterPath: String?,
    val voteAverage: Double?,
    val releaseDate: String?,
    val addedAt: Long
)

fun WatchlistItemEntity.toDomain() = WatchlistItem(
    id = id,
    tmdbId = tmdbId,
    title = title,
    type = MediaType.valueOf(type),
    posterPath = posterPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    addedAt = addedAt
)

fun WatchlistItem.toEntity() = WatchlistItemEntity(
    id = id,
    tmdbId = tmdbId,
    title = title,
    type = type.name,
    posterPath = posterPath,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    addedAt = addedAt
)
