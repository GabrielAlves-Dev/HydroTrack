package com.gabriel.hydrotrack.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GoalViewModel : ViewModel() {
    // Meta diária em ml, valor padrão 2000 ml
    var dailyGoal = mutableStateOf(2000)
        private set

    fun setDailyGoal(newGoal: Int) {
        if (newGoal > 0) {
            dailyGoal.value = newGoal
        }
    }
}
