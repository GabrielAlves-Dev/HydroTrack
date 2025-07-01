package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class WaterUnit(val displayName: String, val mlEquivalent: Float) {
    ML("ml", 1f),
    LITERS("Litros", 1000f),
    CUPS("Copos (250 ml)", 250f),
    BOTTLES("Garrafas (500 ml)", 500f)
}

class UnitViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)

    val selectedUnit = settingsDataStore.waterUnit
        .map { savedOrdinal -> WaterUnit.values().getOrElse(savedOrdinal) { WaterUnit.ML } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = WaterUnit.ML
        )

    fun setUnit(unit: WaterUnit) {
        viewModelScope.launch {
            settingsDataStore.setWaterUnit(unit.ordinal)
        }
    }

    fun convertMlToSelectedUnit(ml: Int, unit: WaterUnit): Float {
        if (unit.mlEquivalent == 0f) return ml.toFloat()
        return ml / unit.mlEquivalent
    }

    fun convertSelectedUnitToMl(amount: Float, unit: WaterUnit): Int {
        return (amount * unit.mlEquivalent).toInt()
    }

    fun getUnitDisplayName(unit: WaterUnit): String {
        return when(unit) {
            WaterUnit.ML -> "ml"
            WaterUnit.LITERS -> "L"
            WaterUnit.CUPS -> "Copos"
            WaterUnit.BOTTLES -> "Garrafas"
        }
    }
}