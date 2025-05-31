package com.gabriel.hydrotrack.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

enum class WaterUnit(val displayName: String, val mlEquivalent: Int) {
    ML("ml", 1),
    CUPS("Copos (250 ml)", 250),
    BOTTLES("Garrafas (1000 ml)", 1000)
}

class UnitViewModel : ViewModel() {
    // Estado da unidade selecionada
    val selectedUnit = mutableStateOf(WaterUnit.ML)

    fun setUnit(unit: WaterUnit) {
        selectedUnit.value = unit
    }
}
