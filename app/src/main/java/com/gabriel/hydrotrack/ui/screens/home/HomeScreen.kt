package com.gabriel.hydrotrack.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabriel.hydrotrack.navigation.Screen
import com.gabriel.hydrotrack.viewmodel.HomeViewModel
import com.gabriel.hydrotrack.viewmodel.ThemeViewModel
import kotlin.math.min
import androidx.compose.material3.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val consumedWater by homeViewModel.consumedWater.collectAsState()
    val dailyGoal by homeViewModel.dailyGoal.collectAsState()
    val progress = min(consumedWater / dailyGoal.toFloat(), 1f)

    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HydroTrack") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Usuário") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.Profile.route)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Configurações") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.Settings.route)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Tema") },
                            onClick = {
                                menuExpanded = false
                                themeViewModel.toggleTheme()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { homeViewModel.addWater(250) }) {
                Text("+")
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
            Text("$consumedWater ml de $dailyGoal ml")
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
        }
    }
}
