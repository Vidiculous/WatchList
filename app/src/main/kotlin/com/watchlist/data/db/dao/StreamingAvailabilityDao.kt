package com.watchlist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.watchlist.data.db.entity.StreamingAvailabilityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamingAvailabilityDao {

    @Query("SELECT * FROM streaming_availability WHERE countryCode = :countryCode")
    fun getByCountry(countryCode: String): Flow<List<StreamingAvailabilityEntity>>

    @Query("DELETE FROM streaming_availability WHERE watchlistItemId = :itemId")
    suspend fun deleteForItem(itemId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<StreamingAvailabilityEntity>)

    @Query("DELETE FROM streaming_availability")
    suspend fun deleteAll()
}
