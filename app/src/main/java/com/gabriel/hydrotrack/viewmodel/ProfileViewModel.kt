package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
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

    // Combina os Flows do DataStore em um único objeto UserProfile
    val userProfile: StateFlow<UserProfile> = combine(
        settingsDataStore.userName,
        settingsDataStore.userEmail,
        settingsDataStore.userPhone
    ) { name: String, email: String, phone: String -> // Tipos especificados para o compilador
        UserProfile(name, email, phone)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserProfile("Carregando...", "", "")
    )

    // Salva os dados atualizados do perfil no DataStore
    fun updateUserProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            settingsDataStore.updateUserProfile(name, email, phone)
        }
    }
}