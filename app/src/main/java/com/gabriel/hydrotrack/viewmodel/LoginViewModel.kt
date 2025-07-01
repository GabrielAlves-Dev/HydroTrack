package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
import com.gabriel.hydrotrack.service.NotificationScheduler
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)
    private val scheduler = NotificationScheduler(application)

    private val mockUsers = mapOf(
        "usuario1@email.com" to "senha123",
        "usuario2@email.com" to "senha456",
        "gabriel@example.com" to "admin"
    )

    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Preencha todos os campos")
            return
        }

        if (mockUsers.containsKey(email) && mockUsers[email] == pass) {
            viewModelScope.launch {
                settingsDataStore.setLoggedInUser(email)
                scheduler.scheduleRepeatingNotifications()
                onSuccess()
            }
        } else {
            onError("Email ou senha inválidos")
        }
    }
}