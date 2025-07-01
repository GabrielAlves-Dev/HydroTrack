package com.gabriel.hydrotrack.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import java.time.LocalDate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val LOGGED_IN_USER_EMAIL = stringPreferencesKey("logged_in_user_email")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

        fun userKey(email: String, key: String) = "${email}_$key"

        fun dailyGoalKey(email: String) = intPreferencesKey(userKey(email, "daily_goal"))
        fun waterUnitKey(email: String) = intPreferencesKey(userKey(email, "water_unit"))
        fun lastConsumptionDateKey(email: String) = stringPreferencesKey(userKey(email, "last_consumption_date"))
        fun dailyConsumptionKey(email: String) = intPreferencesKey(userKey(email, "daily_consumption"))
        fun userNameKey(email: String) = stringPreferencesKey(userKey(email, "user_name"))
        fun userEmailKey(email: String) = stringPreferencesKey(userKey(email, "user_email"))
        fun userPhoneKey(email: String) = stringPreferencesKey(userKey(email, "user_phone"))
    }

    val loggedInUserEmail: Flow<String?> = dataStore.data.map { it[PreferencesKeys.LOGGED_IN_USER_EMAIL] }

    suspend fun setLoggedInUser(email: String) {
        dataStore.edit { it[PreferencesKeys.LOGGED_IN_USER_EMAIL] = email }
    }

    suspend fun clearLoggedInUser() {
        dataStore.edit { it.remove(PreferencesKeys.LOGGED_IN_USER_EMAIL) }
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

    private suspend fun <T> editForCurrentUser(keyBuilder: (String) -> Preferences.Key<T>, value: T) {
        val email = loggedInUserEmail.first()
        if (email != null) {
            dataStore.edit { prefs ->
                prefs[keyBuilder(email)] = value
            }
        }
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.IS_DARK_MODE] ?: false }
    suspend fun toggleDarkMode() {
        dataStore.edit { it[PreferencesKeys.IS_DARK_MODE] = !(it[PreferencesKeys.IS_DARK_MODE] ?: false) }
    }

    val dailyGoal: Flow<Int> = getFlowForCurrentUser(PreferencesKeys::dailyGoalKey, 2000)
    suspend fun setDailyGoal(goal: Int) {
        editForCurrentUser(PreferencesKeys::dailyGoalKey, goal)
    }

    val waterUnit: Flow<Int> = getFlowForCurrentUser(PreferencesKeys::waterUnitKey, 0)
    suspend fun setWaterUnit(unitOrdinal: Int) {
        editForCurrentUser(PreferencesKeys::waterUnitKey, unitOrdinal)
    }

    val dailyConsumption: Flow<Int> = getFlowForCurrentUser(PreferencesKeys::dailyConsumptionKey, 0)
    val lastConsumptionDate: Flow<String> = getFlowForCurrentUser(PreferencesKeys::lastConsumptionDateKey, "")

    suspend fun saveConsumption(amount: Int, date: LocalDate) {
        val email = loggedInUserEmail.first()
        if (email != null) {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.dailyConsumptionKey(email)] = amount
                prefs[PreferencesKeys.lastConsumptionDateKey(email)] = date.toString()
            }
        }
    }

    val userName: Flow<String> = getFlowForCurrentUser(PreferencesKeys::userNameKey, "Usuário")
    val userEmail: Flow<String> = getFlowForCurrentUser(PreferencesKeys::userEmailKey, "")
    val userPhone: Flow<String> = getFlowForCurrentUser(PreferencesKeys::userPhoneKey, "")

    suspend fun updateUserProfile(name: String, email: String, phone: String) {
        val userEmail = loggedInUserEmail.first()
        if (userEmail != null) {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.userNameKey(userEmail)] = name
                prefs[PreferencesKeys.userEmailKey(userEmail)] = email
                prefs[PreferencesKeys.userPhoneKey(userEmail)] = phone
            }
        }
    }
}