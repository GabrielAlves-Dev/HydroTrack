package com.gabriel.hydrotrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WaterRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: WaterRecord)

    @Delete
    suspend fun delete(record: WaterRecord)

    @Query("SELECT * FROM water_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllRecordsForUser(userId: String): Flow<List<WaterRecord>>

    @Query("SELECT * FROM water_records WHERE userId = :userId AND DATE(timestamp) = :date ORDER BY timestamp ASC")
    fun getRecordsForUserAndDate(userId: String, date: LocalDate): Flow<List<WaterRecord>>

    @Query("SELECT SUM(amountMl) FROM water_records WHERE userId = :userId AND DATE(timestamp) = :date")
    fun getTotalConsumptionForUserAndDate(userId: String, date: LocalDate): Flow<Int?>

    @Query("DELETE FROM water_records WHERE userId = :userId")
    suspend fun deleteAllRecordsForUser(userId: String)
}
