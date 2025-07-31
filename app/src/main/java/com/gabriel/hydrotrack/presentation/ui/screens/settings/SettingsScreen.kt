package com.gabriel.hydrotrack.presentation.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gabriel.hydrotrack.navigation.Screen
import com.gabriel.hydrotrack.presentation.viewmodel.SettingsViewModel
import com.gabriel.hydrotrack.presentation.viewmodel.UnitViewModel
import com.gabriel.hydrotrack.presentation.viewmodel.WaterUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel(),
    unitViewModel: UnitViewModel = viewModel()
) {
    val showWeatherSuggestions by settingsViewModel.showWeatherSuggestions.collectAsState()
    val dailyGoal by settingsViewModel.dailyGoal.collectAsState()
    val selectedUnitOrdinal by settingsViewModel.waterUnit.collectAsState()
    val selectedUnit = WaterUnit.values().getOrElse(selectedUnitOrdinal) { WaterUnit.ML }

    var dailyGoalInput by remember(dailyGoal) { mutableStateOf(unitViewModel.convertMlToSelectedUnit(dailyGoal, selectedUnit).toInt().toString()) } // Converte para a unidade selecionada
    var dailyGoalError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Settings.route) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp), // Espaçamento entre os cartões
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cartão para Sugestões de Clima
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Preferências de Exibição",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Mostrar Sugestões de Clima", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = showWeatherSuggestions,
                            onCheckedChange = { settingsViewModel.setShowWeatherSuggestions(it) }
                        )
                    }
                }
            }

            // Cartão para Meta Diária
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Meta Diária de Hidratação",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Defina sua meta diária de ingestão de água:", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dailyGoalInput,
                        onValueChange = { newValue ->
                            dailyGoalInput = newValue.filter { it.isDigit() }
                            dailyGoalError = false
                        },
                        label = { Text("Meta diária (${unitViewModel.getUnitDisplayName(selectedUnit)})") },
                        isError = dailyGoalError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (dailyGoalError) {
                        Text(
                            "Por favor, insira um valor válido maior que zero.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val newGoalFloat = dailyGoalInput.toFloatOrNull()
                            if (newGoalFloat == null || newGoalFloat <= 0) {
                                dailyGoalError = true
                            } else {
                                val newGoalMl = unitViewModel.convertSelectedUnitToMl(newGoalFloat, selectedUnit)
                                settingsViewModel.setDailyGoal(newGoalMl)
                                dailyGoalError = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salvar Meta")
                    }
                }
            }

            // Cartão para Unidade de Medida
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Unidade de Medida",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Selecione a unidade de medida para exibir a quantidade de água:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        WaterUnit.values().forEach { unit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = unit == selectedUnit,
                                        onClick = { settingsViewModel.setWaterUnit(unit.ordinal) },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = unit == selectedUnit,
                                    onClick = null // O onClick do Row já lida com a seleção
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = unit.displayName)
                            }
                        }
                    }
                }
            }
        }
    }
}