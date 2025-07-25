package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.HydroTrackApplication
import com.gabriel.hydrotrack.data.dao.WaterRecordDao
import com.gabriel.hydrotrack.data.model.WaterRecord
import com.gabriel.hydrotrack.data.SettingsDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConsumptionHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val waterRecordDao: WaterRecordDao
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val settingsDataStore = SettingsDataStore(application)

    val unitViewModel = UnitViewModel(application)

    private val _records = MutableStateFlow<List<WaterRecord>>(emptyList())
    val records: StateFlow<List<WaterRecord>> = _records

    init {
        waterRecordDao = (application as HydroTrackApplication).database.waterRecordDao()

        viewModelScope.launch {
            settingsDataStore.loggedInUserEmail.collectLatest { email ->
                val uid = firebaseAuth.currentUser?.uid
                if (email != null && uid != null) {
                    waterRecordDao.getAllRecordsForUser(uid).collectLatest {
                        _records.value = it
                    }
                } else {
                    _records.value = emptyList()
                }
            }
        }
    }

    fun deleteRecord(record: WaterRecord) {
        viewModelScope.launch {
            waterRecordDao.delete(record)
        }
    }
}