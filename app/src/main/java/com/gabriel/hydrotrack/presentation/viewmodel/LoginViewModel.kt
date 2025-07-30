package com.gabriel.hydrotrack.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.gabriel.hydrotrack.service.NotificationScheduler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = UserPreferencesDataStore(application)
    private val scheduler = NotificationScheduler(application)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                auth.signInWithEmailAndPassword(email, pass).await()
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    settingsDataStore.initializeUserData(currentUser.email ?: "", currentUser.displayName)
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

    fun register(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.createUserWithEmailAndPassword(email, pass).await()
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    settingsDataStore.initializeUserData(currentUser.email ?: "", currentUser.displayName)
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
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val googleAccount = GoogleSignIn.getLastSignedInAccount(application.applicationContext)
                    val displayNameFromGoogle = googleAccount?.displayName
                    settingsDataStore.initializeUserData(currentUser.email ?: "", displayNameFromGoogle)
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
            auth.signOut()
            scheduler.cancelNotifications()
        }
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = auth.currentUser
                if (user != null) {
                    user.delete().await()
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