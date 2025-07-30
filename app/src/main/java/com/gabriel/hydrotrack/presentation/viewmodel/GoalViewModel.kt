package com.gabriel.hydrotrack.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = UserPreferencesDataStore(application)

    val dailyGoal = settingsDataStore.dailyGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2000
        )

    fun setDailyGoal(newGoal: Int) {
        if (newGoal > 0) {
            viewModelScope.launch {
                settingsDataStore.setDailyGoal(newGoal)
            }
        }
    }
}