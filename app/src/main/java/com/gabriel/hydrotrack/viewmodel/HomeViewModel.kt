package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.HydroTrackApplication
import com.gabriel.hydrotrack.data.SettingsDataStore
import com.gabriel.hydrotrack.data.dao.WaterRecordDao
import com.gabriel.hydrotrack.data.model.WaterRecord
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)
    private val waterRecordDao: WaterRecordDao
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _consumedWater = MutableStateFlow(0)
    val consumedWater: StateFlow<Int> = _consumedWater

    val dailyGoal: StateFlow<Int> = settingsDataStore.dailyGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2000
        )

    init {
        waterRecordDao = (application as HydroTrackApplication).database.waterRecordDao()

        viewModelScope.launch {
            settingsDataStore.loggedInUserEmail.collectLatest { email ->
                val uid = firebaseAuth.currentUser?.uid
                if (email != null && uid != null) {
                    settingsDataStore.syncDataFromFirebaseToLocal(email)

                    waterRecordDao.getTotalConsumptionForUserAndDate(uid, LocalDate.now()).collectLatest { totalMl ->
                        _consumedWater.value = totalMl ?: 0
                        settingsDataStore.saveConsumption(_consumedWater.value, LocalDate.now())
                    }
                } else {
                    _consumedWater.value = 0
                }
            }
        }
        checkDateAndResetConsumption()
    }

    private fun checkDateAndResetConsumption() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val lastDate = settingsDataStore.lastConsumptionDate.first()

            if (today != lastDate) {
                settingsDataStore.saveConsumption(0, LocalDate.now())
            }
        }
    }

    fun addWater(amountMl: Int) {
        val uid = firebaseAuth.currentUser?.uid
        if (amountMl > 0 && uid != null) {
            viewModelScope.launch {
                val newRecord = WaterRecord(
                    userId = uid,
                    amountMl = amountMl,
                    timestamp = LocalDateTime.now()
                )
                waterRecordDao.insert(newRecord)
            }
        }
    }

    fun removeWater(amountMl: Int) {
        val uid = firebaseAuth.currentUser?.uid
        if (amountMl > 0 && uid != null) {
            viewModelScope.launch {
                val newRecord = WaterRecord(
                    userId = uid,
                    amountMl = -amountMl,
                    timestamp = LocalDateTime.now()
                )
                waterRecordDao.insert(newRecord)
            }
        }
    }
}