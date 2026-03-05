package com.watchlist.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.watchlist.data.model.StreamingAvailability

@Entity(
    tableName = "streaming_availability",
    foreignKeys = [ForeignKey(
        entity = WatchlistItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["watchlistItemId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("watchlistItemId")]
)
data class StreamingAvailabilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val watchlistItemId: Long,
    val serviceName: String,
    val serviceLogoPath: String?,
    val countryCode: String,
    val fetchedAt: Long
)

fun StreamingAvailabilityEntity.toDomain() = StreamingAvailability(
    id = id,
    watchlistItemId = watchlistItemId,
    serviceName = serviceName,
    serviceLogoPath = serviceLogoPath,
    countryCode = countryCode,
    fetchedAt = fetchedAt
)
