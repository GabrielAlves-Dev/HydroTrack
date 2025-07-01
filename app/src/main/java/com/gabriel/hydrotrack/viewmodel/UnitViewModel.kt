package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class WaterUnit(val displayName: String, val mlEquivalent: Int) {
    ML("ml", 1),
    CUPS("Copos (250 ml)", 250),
    BOTTLES("Garrafas (1000 ml)", 1000)
}

class UnitViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)

    // Lê a unidade salva do DataStore e a converte para o enum WaterUnit
    val selectedUnit = settingsDataStore.waterUnit
        .map { savedOrdinal -> WaterUnit.values().getOrElse(savedOrdinal) { WaterUnit.ML } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = WaterUnit.ML
        )

    // Salva a nova unidade no DataStore usando sua posição ordinal (índice)
    fun setUnit(unit: WaterUnit) {
        viewModelScope.launch {
            settingsDataStore.setWaterUnit(unit.ordinal)
        }
    }
}