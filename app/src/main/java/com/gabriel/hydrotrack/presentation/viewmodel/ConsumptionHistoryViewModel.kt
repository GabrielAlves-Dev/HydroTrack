package com.gabriel.hydrotrack.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.HydroTrackApplication
import com.gabriel.hydrotrack.data.local.dao.WaterRecord
import com.gabriel.hydrotrack.data.local.dao.WaterRecordDao
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConsumptionHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val waterRecordDao: WaterRecordDao
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val settingsDataStore = UserPreferencesDataStore(application)

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