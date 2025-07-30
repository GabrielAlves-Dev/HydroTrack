package com.gabriel.hydrotrack.presentation.ui.screens.unit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gabriel.hydrotrack.presentation.viewmodel.UnitViewModel
import com.gabriel.hydrotrack.presentation.viewmodel.WaterUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitScreen(
    navController: NavController,
    unitViewModel: UnitViewModel = viewModel()
) {
    val selectedUnit by unitViewModel.selectedUnit.collectAsState()

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