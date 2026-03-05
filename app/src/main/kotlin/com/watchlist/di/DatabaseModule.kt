package com.watchlist.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.watchlist.data.db.AppDatabase
import com.watchlist.data.db.dao.StreamingAvailabilityDao
import com.watchlist.data.db.dao.WatchlistItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN voteAverage REAL")
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN releaseDate TEXT")
    }
}

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN watched INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE watchlist_items ADD COLUMN userRating INTEGER")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "watchlist.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.execSQL("PRAGMA foreign_keys=ON")
                }
            })
            .build()

    @Provides
    fun provideWatchlistItemDao(db: AppDatabase): WatchlistItemDao =
        db.watchlistItemDao()

    @Provides
    fun provideStreamingAvailabilityDao(db: AppDatabase): StreamingAvailabilityDao =
        db.streamingAvailabilityDao()
}
