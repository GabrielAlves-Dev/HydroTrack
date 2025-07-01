package com.gabriel.hydrotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.data.SettingsDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)

    // Consumo de água do dia, agora lido do DataStore
    private val _consumedWater = MutableStateFlow(0)
    val consumedWater: StateFlow<Int> = _consumedWater

    // Meta diária de consumo, lida do DataStore
    val dailyGoal: StateFlow<Int> = settingsDataStore.dailyGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2000
        )

    // Bloco de inicialização: é executado quando o ViewModel é criado
    init {
        checkDateAndResetConsumption()
    }

    private fun checkDateAndResetConsumption() {
        viewModelScope.launch {
            // Pega a data de hoje
            val today = LocalDate.now().toString()

            // Lê a última data salva e o último consumo salvo
            val lastDate = settingsDataStore.lastConsumptionDate.first()
            val lastConsumption = settingsDataStore.dailyConsumption.first()

            if (today != lastDate) {
                // Se a data for diferente, zera o consumo e salva
                _consumedWater.value = 0
                settingsDataStore.saveConsumption(0, LocalDate.now())
            } else {
                // Se for o mesmo dia, apenas carrega o consumo salvo
                _consumedWater.value = lastConsumption
            }
        }
    }

    // Adiciona uma quantidade de água e salva o novo total e a data de hoje
    fun addWater(amount: Int) {
        if (amount > 0) {
            viewModelScope.launch {
                val newTotal = _consumedWater.value + amount
                _consumedWater.value = newTotal
                settingsDataStore.saveConsumption(newTotal, LocalDate.now())
            }
        }
    }
}