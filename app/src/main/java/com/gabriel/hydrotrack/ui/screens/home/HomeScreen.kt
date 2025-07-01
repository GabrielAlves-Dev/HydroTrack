package com.gabriel.hydrotrack.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gabriel.hydrotrack.navigation.Screen
import com.gabriel.hydrotrack.viewmodel.HomeViewModel
import com.gabriel.hydrotrack.viewmodel.ThemeViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    homeViewModel: HomeViewModel = viewModel()
) {
    val consumedWater by homeViewModel.consumedWater.collectAsState()
    val dailyGoal by homeViewModel.dailyGoal.collectAsState()
    val progress = if (dailyGoal > 0) min(consumedWater / dailyGoal.toFloat(), 1f) else 0f

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "Progress Animation")

    var menuExpanded by remember { mutableStateOf(false) }
    var showCustomAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HydroTrack") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text("Usuário") }, onClick = {
                            menuExpanded = false
                            navController.navigate(Screen.Profile.route)
                        })
                        DropdownMenuItem(text = { Text("Configurações") }, onClick = {
                            menuExpanded = false
                            navController.navigate(Screen.Settings.route)
                        })
                        DropdownMenuItem(text = { Text("Alternar Tema") }, onClick = {
                            menuExpanded = false
                            themeViewModel.toggleTheme()
                        })
                    }
                }
            )
        },
        floatingActionButton = {
            // Botão "+" agora abre o diálogo de adição personalizada
            FloatingActionButton(onClick = { showCustomAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar água")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Consumo de Água", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Text("$consumedWater ml / $dailyGoal ml", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(progress = animatedProgress, modifier = Modifier.fillMaxWidth().height(8.dp))
        }
    }

    // O diálogo permanece o mesmo
    if (showCustomAddDialog) {
        CustomWaterAddDialog(
            onDismiss = { showCustomAddDialog = false },
            onConfirm = { amount ->
                homeViewModel.addWater(amount)
                showCustomAddDialog = false
            }
        )
    }
}

@Composable
private fun CustomWaterAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf("") }
    val isError = textValue.toIntOrNull() == null && textValue.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Água") },
        text = {
            Column {
                Text("Digite a quantidade em ml que você bebeu:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it.filter { char -> char.isDigit() } },
                    label = { Text("Quantidade (ml)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isError) {
                    Text("Por favor, insira um número válido.", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(textValue.toIntOrNull() ?: 0) },
                enabled = textValue.isNotEmpty() && !isError
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}