package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)

    val dailyGoal = settingsDataStore.dailyGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2000 // Valor inicial enquanto o DataStore carrega
        )

    fun setDailyGoal(newGoal: Int) {
        if (newGoal > 0) {
            viewModelScope.launch {
                settingsDataStore.setDailyGoal(newGoal)
            }
        }
    }
}