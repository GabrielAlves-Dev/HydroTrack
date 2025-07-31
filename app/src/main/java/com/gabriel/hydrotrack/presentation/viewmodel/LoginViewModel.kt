package com.gabriel.hydrotrack.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.gabriel.hydrotrack.data.repository.AuthRepositoryImpl
import com.gabriel.hydrotrack.data.repository.IAuthRepository
import com.gabriel.hydrotrack.service.NotificationScheduler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: IAuthRepository = AuthRepositoryImpl(FirebaseAuth.getInstance())
    private val settingsDataStore: UserPreferencesDataStore = UserPreferencesDataStore(application)
    private val scheduler: NotificationScheduler = NotificationScheduler(application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.login(email, pass)
                val currentUserEmail = authRepository.currentUserEmail
                val currentUserDisplayName = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.displayName
                if (currentUserEmail != null) {
                    settingsDataStore.initializeUserData(currentUserEmail, currentUserDisplayName)
                }
                scheduler.scheduleRepeatingNotifications()
                onSuccess()
            } catch (e: Exception) {
                onError("Erro ao fazer login: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) { // Adicionado 'name'
        if (name.isBlank() || email.isBlank() || pass.isBlank()) { // Validação do nome
            onError("Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.register(email, pass)
                val currentUserEmail = authRepository.currentUserEmail
                val displayNameToSave = if (name.isNotBlank()) name else currentUserEmail?.substringBefore("@")
                if (currentUserEmail != null) {
                    settingsDataStore.initializeUserData(currentUserEmail, displayNameToSave)
                }
                scheduler.scheduleRepeatingNotifications()
                onSuccess()
            } catch (e: Exception) {
                onError("Erro ao registrar: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInWithGoogleCredential(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.signInWithGoogleCredential(idToken)
                val currentUserEmail = authRepository.currentUserEmail
                val googleAccount = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)
                val displayNameFromGoogle = googleAccount?.displayName
                if (currentUserEmail != null) {
                    settingsDataStore.initializeUserData(currentUserEmail, displayNameFromGoogle)
                }
                scheduler.scheduleRepeatingNotifications()
                onSuccess()
            } catch (e: Exception) {
                onError("Erro ao fazer login com Google: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            scheduler.cancelNotifications()
        }
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (authRepository.currentUserId != null) {
                    authRepository.deleteAccount()
                    scheduler.cancelNotifications()
                    onSuccess()
                } else {
                    onError("Nenhum usuário logado para excluir.")
                }
            } catch (e: Exception) {
                onError("Erro ao excluir conta: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}