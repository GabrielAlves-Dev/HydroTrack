package com.gabriel.hydrotrack.presentation.ui.screens.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gabriel.hydrotrack.presentation.viewmodel.GoalViewModel
import com.gabriel.hydrotrack.presentation.viewmodel.UnitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    navController: NavController,
    goalViewModel: GoalViewModel = viewModel(),
    unitViewModel: UnitViewModel = viewModel()
) {
    val dailyGoalMl by goalViewModel.dailyGoal.collectAsState()
    val selectedUnit by unitViewModel.selectedUnit.collectAsState()
    val unitLabel = unitViewModel.getUnitDisplayName(selectedUnit)

    val convertedGoal = unitViewModel.convertMlToSelectedUnit(dailyGoalMl, selectedUnit)
    var inputValue by remember(convertedGoal) { mutableStateOf(convertedGoal.toInt().toString()) }
    var error by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meta Diária") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Defina sua meta diária de ingestão de água:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = inputValue,
                onValueChange = { newValue ->
                    inputValue = newValue.filter { it.isDigit() }
                    error = false
                },
                label = { Text("Meta diária ($unitLabel)") },
                isError = error,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (error) {
                Text(
                    "Por favor, insira um valor válido maior que zero.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val newGoalFloat = inputValue.toFloatOrNull()
                    if (newGoalFloat == null || newGoalFloat <= 0) {
                        error = true
                    } else {
                        val newGoalMl = unitViewModel.convertSelectedUnitToMl(newGoalFloat, selectedUnit)
                        goalViewModel.setDailyGoal(newGoalMl)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    }
}