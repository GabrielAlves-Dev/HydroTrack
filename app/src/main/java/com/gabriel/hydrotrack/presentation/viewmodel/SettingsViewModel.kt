package com.gabriel.hydrotrack.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = UserPreferencesDataStore(application)

    val showWeatherSuggestions = settingsDataStore.showWeatherSuggestions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    fun setShowWeatherSuggestions(enable: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowWeatherSuggestions(enable)
        }
    }

    val dailyGoal = settingsDataStore.dailyGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2000
        )

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch {
            settingsDataStore.setDailyGoal(goal)
        }
    }

    val waterUnit = settingsDataStore.waterUnit
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    fun setWaterUnit(unitOrdinal: Int) {
        viewModelScope.launch {
            settingsDataStore.setWaterUnit(unitOrdinal)
        }
    }
}