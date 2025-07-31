package com.gabriel.hydrotrack.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : IAuthRepository {

    private val firebaseDb = FirebaseDatabase.getInstance().reference

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override val currentUserEmail: String?
        get() = firebaseAuth.currentUser?.email

    override suspend fun login(email: String, pass: String) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).await()
    }

    override suspend fun register(email: String, pass: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
    }

    override suspend fun signInWithGoogleCredential(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun deleteAccount() {
        firebaseAuth.currentUser?.delete()?.await()
    }

    override suspend fun deleteUserDataFromDatabase(uid: String) {
        firebaseDb.child("users").child(uid).removeValue().await()
    }
}