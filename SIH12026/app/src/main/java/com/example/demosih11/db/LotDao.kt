package com.example.demosih11.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LotDao {
    @Query("SELECT * FROM lots ORDER BY timestamp DESC")
    fun getAllLots(): Flow<List<Lot>>

    @Insert
    suspend fun insertLot(lot: Lot): Long

    @Query("SELECT SUM(estimatedValue) FROM lots WHERE status = 'HANDED_OVER'")
    fun getTotalEarnings(): Flow<Double?>
}