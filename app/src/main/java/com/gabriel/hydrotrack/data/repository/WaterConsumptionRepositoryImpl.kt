package com.gabriel.hydrotrack.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.gabriel.hydrotrack.data.local.dao.WaterRecord
import com.gabriel.hydrotrack.data.local.dao.WaterRecordDao
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

class WaterConsumptionRepositoryImpl(
    private val waterRecordDao: WaterRecordDao,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val firebaseAuth: FirebaseAuth
) : IWaterConsumptionRepository {

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addWaterRecord(amountMl: Int) {
        val userId = currentUserId ?: return
        val newRecord = WaterRecord(
            userId = userId,
            amountMl = amountMl,
            timestamp = LocalDateTime.now()
        )
        waterRecordDao.insert(newRecord)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun removeWaterRecord(amountMl: Int) {
        val userId = currentUserId ?: return
        val newRecord = WaterRecord(
            userId = userId,
            amountMl = -amountMl,
            timestamp = LocalDateTime.now()
        )
        waterRecordDao.insert(newRecord)
    }

    override suspend fun deleteWaterRecord(record: WaterRecord) {
        waterRecordDao.delete(record)
    }

    override fun getAllRecordsForUser(): Flow<List<WaterRecord>> {
        val userId = currentUserId
        return if (userId != null) {
            waterRecordDao.getAllRecordsForUser(userId)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }

    override fun getTotalConsumptionForUserAndDate(date: LocalDate): Flow<Int?> {
        val userId = currentUserId
        return if (userId != null) {
            waterRecordDao.getTotalConsumptionForUserAndDate(userId, date)
        } else {
            kotlinx.coroutines.flow.flowOf(0)
        }
    }

    override suspend fun saveDailyConsumptionLocally(amount: Int, date: LocalDate) {
        val userId = currentUserId
        if (userId != null) {
            userPreferencesDataStore.saveConsumption(amount, date)
        }
    }
}