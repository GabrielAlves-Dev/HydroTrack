package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
import com.gabriel.hydrotrack.service.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)
    private val scheduler = NotificationScheduler(application)

    val userProfile: StateFlow<UserProfile> = combine(
        settingsDataStore.userName,
        settingsDataStore.loggedInUserEmail,
        settingsDataStore.userPhone
    ) { name, email, phone ->
        UserProfile(name, email ?: "", phone)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserProfile("Carregando...", "", "")
    )

    fun updateUserProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            settingsDataStore.updateUserProfile(name, email, phone)
        }
    }

    fun logout() {
        viewModelScope.launch {
            scheduler.cancelNotifications()
            settingsDataStore.clearLoggedInUser()
        }
    }
}