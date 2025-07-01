package com.gabriel.hydrotrack.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context) {

    private val dataStore = context.dataStore

    // Objeto para guardar TODAS as chaves do aplicativo
    private object PreferencesKeys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val DAILY_GOAL = intPreferencesKey("daily_goal")
        val WATER_UNIT = intPreferencesKey("water_unit")

        // Chaves de consumo
        val LAST_CONSUMPTION_DATE = stringPreferencesKey("last_consumption_date")
        val DAILY_CONSUMPTION = intPreferencesKey("daily_consumption")

        // Chaves do Perfil do Usuário
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
    }

    // --- MODO ESCURO ---
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.IS_DARK_MODE] ?: false }
    suspend fun toggleDarkMode() {
        dataStore.edit { it[PreferencesKeys.IS_DARK_MODE] = !(it[PreferencesKeys.IS_DARK_MODE] ?: false) }
    }

    // --- META DIÁRIA ---
    val dailyGoal: Flow<Int> = dataStore.data.map { it[PreferencesKeys.DAILY_GOAL] ?: 2000 }
    suspend fun setDailyGoal(goal: Int) {
        dataStore.edit { it[PreferencesKeys.DAILY_GOAL] = goal }
    }

    // --- UNIDADE DE MEDIDA ---
    val waterUnit: Flow<Int> = dataStore.data.map { it[PreferencesKeys.WATER_UNIT] ?: 0 }
    suspend fun setWaterUnit(unitOrdinal: Int) {
        dataStore.edit { it[PreferencesKeys.WATER_UNIT] = unitOrdinal }
    }

    // --- CONTROLE DE CONSUMO DIÁRIO ---
    val dailyConsumption: Flow<Int> = dataStore.data.map { it[PreferencesKeys.DAILY_CONSUMPTION] ?: 0 }
    val lastConsumptionDate: Flow<String> = dataStore.data.map { it[PreferencesKeys.LAST_CONSUMPTION_DATE] ?: "" }

    suspend fun saveConsumption(amount: Int, date: LocalDate) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.DAILY_CONSUMPTION] = amount
            prefs[PreferencesKeys.LAST_CONSUMPTION_DATE] = date.toString()
        }
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