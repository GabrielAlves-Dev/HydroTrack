package com.gabriel.hydrotrack.ui.screens.unit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabriel.hydrotrack.viewmodel.UnitViewModel
import com.gabriel.hydrotrack.viewmodel.WaterUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitScreen(
    navController: NavController,
    unitViewModel: UnitViewModel
) {
    val selectedUnit by unitViewModel.selectedUnit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unidade de Medida") },
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
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Selecione a unidade de medida para exibir a quantidade de água:",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            WaterUnit.values().forEach { unit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = unit == selectedUnit,
                        onClick = { unitViewModel.setUnit(unit) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = unit.displayName)
                }
            }
        }
    }
}

