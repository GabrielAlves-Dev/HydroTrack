package com.gabriel.hydrotrack.data.repository

import com.gabriel.hydrotrack.data.local.dao.WaterRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface IWaterConsumptionRepository {
    val currentUserId: String?

    suspend fun addWaterRecord(amountMl: Int)

    suspend fun removeWaterRecord(amountMl: Int)

    suspend fun deleteWaterRecord(record: WaterRecord)

    fun getAllRecordsForUser(): Flow<List<WaterRecord>>

    fun getTotalConsumptionForUserAndDate(date: LocalDate): Flow<Int?>

    suspend fun saveDailyConsumptionLocally(amount: Int, date: LocalDate)
}