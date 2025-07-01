package com.gabriel.hydrotrack.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context) {

    private val dataStore = context.dataStore

    // Objeto para guardar todas as chaves de forma segura
    private object PreferencesKeys {
        // Configurações
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

        // Dados do Usuário
        val DAILY_GOAL = intPreferencesKey("daily_goal")
        val WATER_UNIT = intPreferencesKey("water_unit")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
    }

    // --- MODO ESCURO ---
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.IS_DARK_MODE] ?: false }
    suspend fun toggleDarkMode() {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_DARK_MODE] = !(prefs[PreferencesKeys.IS_DARK_MODE] ?: false)
        }
    }

    // --- META DIÁRIA ---
    val dailyGoal: Flow<Int> = dataStore.data.map { it[PreferencesKeys.DAILY_GOAL] ?: 2000 }
    suspend fun setDailyGoal(goal: Int) {
        dataStore.edit { it[PreferencesKeys.DAILY_GOAL] = goal }
    }

    // --- UNIDADE DE MEDIDA ---
    val waterUnit: Flow<Int> = dataStore.data.map { it[PreferencesKeys.WATER_UNIT] ?: 0 } // Padrão: 0 (ML)
    suspend fun setWaterUnit(unitOrdinal: Int) {
        dataStore.edit { it[PreferencesKeys.WATER_UNIT] = unitOrdinal }
    }

    // --- PERFIL DO USUÁRIO ---
    val userName: Flow<String> = dataStore.data.map { it[PreferencesKeys.USER_NAME] ?: "Gabriel Silva" }
    val userEmail: Flow<String> = dataStore.data.map { it[PreferencesKeys.USER_EMAIL] ?: "gabriel@example.com" }
    val userPhone: Flow<String> = dataStore.data.map { it[PreferencesKeys.USER_PHONE] ?: "(11) 99999-9999" }

    suspend fun updateUserProfile(name: String, email: String, phone: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_NAME] = name
            prefs[PreferencesKeys.USER_EMAIL] = email
            prefs[PreferencesKeys.USER_PHONE] = phone
        }
    }
}