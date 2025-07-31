package com.gabriel.hydrotrack.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface IAuthRepository {
    val currentUserId: String?

    val currentUserEmail: String?

    suspend fun login(email: String, pass: String)

    suspend fun register(email: String, pass: String)

    suspend fun signInWithGoogleCredential(idToken: String)

    suspend fun logout()

    suspend fun deleteAccount()
}