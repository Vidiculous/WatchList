package com.watchlist.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.watchlist.data.db.dao.StreamingAvailabilityDao
import com.watchlist.data.db.dao.WatchlistItemDao
import com.watchlist.data.db.entity.StreamingAvailabilityEntity
import com.watchlist.data.db.entity.WatchlistItemEntity

@Database(
    entities = [WatchlistItemEntity::class, StreamingAvailabilityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistItemDao(): WatchlistItemDao
    abstract fun streamingAvailabilityDao(): StreamingAvailabilityDao
}
