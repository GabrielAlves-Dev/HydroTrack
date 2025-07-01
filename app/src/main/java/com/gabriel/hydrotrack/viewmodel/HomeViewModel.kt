package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

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

    fun addWater(amount: Int) {
        if (amount > 0) {
            _consumedWater.value += amount
        }
    }

    fun resetConsumption() {
        _consumedWater.value = 0
    }
}