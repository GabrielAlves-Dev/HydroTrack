package com.gabriel.hydrotrack.data.repository

import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val firebaseAuth: FirebaseAuth
) : IUserRepository {

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override val userName: Flow<String>
        get() = userPreferencesDataStore.userName

    override val userEmail: Flow<String>
        get() = userPreferencesDataStore.userEmail

    override val userPhone: Flow<String>
        get() = userPreferencesDataStore.userPhone

    override suspend fun updateUserProfile(name: String, email: String, phone: String) {
        userPreferencesDataStore.updateUserProfile(name, email, phone)
    }
}