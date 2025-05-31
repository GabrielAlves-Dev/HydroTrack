package com.gabriel.hydrotrack.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    // Quantidade de água consumida no dia (em ml)
    private val _consumedWater = MutableStateFlow(0)
    val consumedWater: StateFlow<Int> = _consumedWater

    // Meta diária de consumo (em ml)
    private val _dailyGoal = MutableStateFlow(2000)
    val dailyGoal: StateFlow<Int> = _dailyGoal

    // Adiciona uma quantidade de água ao total consumido
    fun addWater(amount: Int) {
        _consumedWater.value += amount
    }

    // Reinicia o consumo diário (opcional)
    fun resetConsumption() {
        _consumedWater.value = 0
    }

    // Atualiza a meta diária (opcional)
    fun setDailyGoal(goal: Int) {
        _dailyGoal.value = goal
    }
}
