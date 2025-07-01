package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)

    private val _consumedWater = MutableStateFlow(0)
    val consumedWater: StateFlow<Int> = _consumedWater

    val dailyGoal: StateFlow<Int> = settingsDataStore.dailyGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2000
        )

    init {
        checkDateAndResetConsumption()
    }

    private fun checkDateAndResetConsumption() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val lastDate = settingsDataStore.lastConsumptionDate.first()
            val lastConsumption = settingsDataStore.dailyConsumption.first()

            if (today != lastDate) {
                _consumedWater.value = 0
                settingsDataStore.saveConsumption(0, LocalDate.now())
            } else {
                _consumedWater.value = lastConsumption
            }
        }
    }

    fun addWater(amount: Int) {
        if (amount > 0) {
            viewModelScope.launch {
                val newTotal = _consumedWater.value + amount
                _consumedWater.value = newTotal
                settingsDataStore.saveConsumption(newTotal, LocalDate.now())
            }
        }
    }

    fun removeWater(amount: Int) {
        if (amount > 0) {
            viewModelScope.launch {
                val newTotal = (_consumedWater.value - amount).coerceAtLeast(0)
                _consumedWater.value = newTotal
                settingsDataStore.saveConsumption(newTotal, LocalDate.now())
            }
        }
    }
}