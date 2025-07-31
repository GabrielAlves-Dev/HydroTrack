package com.gabriel.hydrotrack.data.repository

import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    val currentUserId: String?
    val userName: Flow<String>
    val userEmail: Flow<String>
    val userPhone: Flow<String>
    suspend fun updateUserProfile(name: String, email: String, phone: String)
}