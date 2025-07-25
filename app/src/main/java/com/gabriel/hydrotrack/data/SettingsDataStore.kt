package com.gabriel.hydrotrack.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private val dataStore = context.dataStore
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDb: DatabaseReference = FirebaseDatabase.getInstance().reference

    private object PreferencesKeys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

        fun userKey(email: String, key: String) = "${email.replace(".", "_")}_$key"

        fun dailyGoalKey(email: String) = intPreferencesKey(userKey(email, "daily_goal"))
        fun waterUnitKey(email: String) = intPreferencesKey(userKey(email, "water_unit"))
        fun lastConsumptionDateKey(email: String) = stringPreferencesKey(userKey(email, "last_consumption_date"))
        fun dailyConsumptionKey(email: String) = intPreferencesKey(userKey(email, "daily_consumption"))
        fun userNameKey(email: String) = stringPreferencesKey(userKey(email, "user_name"))
        fun userEmailKey(email: String) = stringPreferencesKey(userKey(email, "user_email"))
        fun userPhoneKey(email: String) = stringPreferencesKey(userKey(email, "user_phone"))
    }

    val loggedInUserEmail: Flow<String?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.email)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }.distinctUntilChanged()

    suspend fun initializeUserData(userEmail: String, displayName: String?) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.userEmailKey(userEmail)] = userEmail

            val currentName = prefs[PreferencesKeys.userNameKey(userEmail)]
            if (currentName.isNullOrBlank() || currentName == "Usuário") {
                prefs[PreferencesKeys.userNameKey(userEmail)] = displayName ?: userEmail.substringBefore("@")
            }

            val currentPhone = prefs[PreferencesKeys.userPhoneKey(userEmail)]
            if (currentPhone.isNullOrBlank()) {
                prefs[PreferencesKeys.userPhoneKey(userEmail)] = ""
            }
        }
        syncDataFromFirebaseToLocal(userEmail)
    }

    suspend fun syncDataFromFirebaseToLocal(userEmail: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return

        try {
            val userRef = firebaseDb.child("users").child(uid).get().await()
            val firebaseDailyGoal = userRef.child("dailyGoal").getValue(Int::class.java)
            val firebaseDailyConsumption = userRef.child("dailyConsumption").getValue(Int::class.java)
            val firebaseLastConsumptionDate = userRef.child("lastConsumptionDate").getValue(String::class.java)

            dataStore.edit { prefs ->
                if (firebaseDailyGoal != null) {
                    prefs[PreferencesKeys.dailyGoalKey(userEmail)] = firebaseDailyGoal
                }
                if (firebaseDailyConsumption != null) {
                    prefs[PreferencesKeys.dailyConsumptionKey(userEmail)] = firebaseDailyConsumption
                }
                if (firebaseLastConsumptionDate != null) {
                    prefs[PreferencesKeys.lastConsumptionDateKey(userEmail)] = firebaseLastConsumptionDate
                }
            }
        } catch (e: Exception) {
        }
    }

    private suspend fun <T> editForCurrentUser(keyBuilder: (String) -> Preferences.Key<T>, value: T) {
        val email = loggedInUserEmail.first()
        if (email != null) {
            dataStore.edit { prefs ->
                prefs[keyBuilder(email)] = value
            }
        }
    }

    private fun <T> getFlowForCurrentUser(
        keyBuilder: (String) -> Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> {
        return loggedInUserEmail.flatMapLatest { email ->
            if (email != null) {
                dataStore.data.map { preferences ->
                    preferences[keyBuilder(email)] ?: defaultValue
                }
            } else {
                flowOf(defaultValue)
            }
        }
    }

    private suspend fun <T> editAndSyncForCurrentUser(
        keyBuilder: (String) -> Preferences.Key<T>,
        firebasePath: String,
        value: T
    ) {
        val email = loggedInUserEmail.first()
        val uid = firebaseAuth.currentUser?.uid

        if (email != null && uid != null) {
            dataStore.edit { prefs ->
                prefs[keyBuilder(email)] = value
            }
            firebaseDb.child("users").child(uid).child(firebasePath).setValue(value).await()
        }
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.IS_DARK_MODE] ?: false }
    suspend fun toggleDarkMode() {
        dataStore.edit { it[PreferencesKeys.IS_DARK_MODE] = !(it[PreferencesKeys.IS_DARK_MODE] ?: false) }
    }

    val dailyGoal: Flow<Int> = getFlowForCurrentUser(PreferencesKeys::dailyGoalKey, 2000)
    suspend fun setDailyGoal(goal: Int) {
        editAndSyncForCurrentUser(PreferencesKeys::dailyGoalKey, "dailyGoal", goal)
    }

    val waterUnit: Flow<Int> = getFlowForCurrentUser(PreferencesKeys::waterUnitKey, 0)
    suspend fun setWaterUnit(unitOrdinal: Int) {
        editForCurrentUser(PreferencesKeys::waterUnitKey, unitOrdinal)
    }

    val dailyConsumption: Flow<Int> = getFlowForCurrentUser(PreferencesKeys::dailyConsumptionKey, 0)
    val lastConsumptionDate: Flow<String> = getFlowForCurrentUser(PreferencesKeys::lastConsumptionDateKey, "")

    suspend fun saveConsumption(amount: Int, date: LocalDate) {
        val email = loggedInUserEmail.first()
        val uid = firebaseAuth.currentUser?.uid
        if (email != null && uid != null) {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.dailyConsumptionKey(email)] = amount
                prefs[PreferencesKeys.lastConsumptionDateKey(email)] = date.toString()
            }
            firebaseDb.child("users").child(uid).child("dailyConsumption").setValue(amount).await()
            firebaseDb.child("users").child(uid).child("lastConsumptionDate").setValue(date.toString()).await()
        }
    }

    val userName: Flow<String> = getFlowForCurrentUser(PreferencesKeys::userNameKey, "Usuário")
    val userEmail: Flow<String> = getFlowForCurrentUser(PreferencesKeys::userEmailKey, "")
    val userPhone: Flow<String> = getFlowForCurrentUser(PreferencesKeys::userPhoneKey, "")

    suspend fun updateUserProfile(name: String, email: String, phone: String) {
        val userEmailKeyForPrefs = firebaseAuth.currentUser?.email
        val uid = firebaseAuth.currentUser?.uid
        if (userEmailKeyForPrefs != null && uid != null) {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.userNameKey(userEmailKeyForPrefs)] = name
                prefs[PreferencesKeys.userEmailKey(userEmailKeyForPrefs)] = email
                prefs[PreferencesKeys.userPhoneKey(userEmailKeyForPrefs)] = phone
            }
            val userProfileUpdates = mapOf(
                "userName" to name,
                "userEmail" to email,
                "userPhone" to phone
            )
            firebaseDb.child("users").child(uid).updateChildren(userProfileUpdates).await()
        }
    }
}