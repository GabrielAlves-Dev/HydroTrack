package com.gabriel.hydrotrack.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String
)

class ProfileViewModel : ViewModel() {

    // Dados simulados do usuário
    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Gabriel Silva",
            email = "gabriel@example.com",
            phone = "(11) 99999-9999"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile

    fun updateUserProfile(name: String, email: String, phone: String) {
        _userProfile.value = UserProfile(name, email, phone)
    }
    // Futuras funções para atualizar os dados do usuário podem ser adicionadas aqui
}
