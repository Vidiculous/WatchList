package com.watchlist.di

import android.content.Context
import androidx.room.Room
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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "watchlist.db")
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
