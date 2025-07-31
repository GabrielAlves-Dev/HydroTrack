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

class ConsumptionHistoryViewModel(
    application: Application,
    private val waterRecordDao: WaterRecordDao,
    private val firebaseAuth: FirebaseAuth,
    private val settingsDataStore: UserPreferencesDataStore
) : AndroidViewModel(application) {

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ConsumptionHistoryViewModel::class.java)) {
                        val appDatabase = (application as HydroTrackApplication).database
                        val waterRecordDao = appDatabase.waterRecordDao()
                        val firebaseAuth = FirebaseAuth.getInstance()
                        val settingsDataStore = UserPreferencesDataStore(application)

                        return ConsumptionHistoryViewModel(
                            application,
                            waterRecordDao,
                            firebaseAuth,
                            settingsDataStore
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }

    val unitViewModel = UnitViewModel(application)

    private val _records = MutableStateFlow<List<WaterRecord>>(emptyList())
    val records: StateFlow<List<WaterRecord>> = _records

    init {
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