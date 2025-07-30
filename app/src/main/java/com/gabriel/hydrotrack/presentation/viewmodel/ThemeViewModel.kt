package com.gabriel.hydrotrack.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = UserPreferencesDataStore(application)

    val isDarkTheme = settingsDataStore.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun toggleTheme() {
        viewModelScope.launch {
            settingsDataStore.toggleDarkMode()
        }
    }
}