package com.gabriel.hydrotrack.presentation.ui.screens.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gabriel.hydrotrack.presentation.viewmodel.ConsumptionHistoryViewModel
import com.gabriel.hydrotrack.presentation.viewmodel.UnitViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ConsumptionHistoryScreen(
    navController: NavController,
    historyViewModel: ConsumptionHistoryViewModel = viewModel(),
    unitViewModel: UnitViewModel = viewModel()
) {
    val records by historyViewModel.records.collectAsState()
    val selectedUnit by unitViewModel.selectedUnit.collectAsState()
    val unitLabel = unitViewModel.getUnitDisplayName(selectedUnit)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Consumo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Nenhum registro de consumo ainda.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records, key = { it.id }) { record ->
                    val coroutineScope = rememberCoroutineScope()
                    val offsetX = remember { Animatable(0f) }
                    val itemWidthPx = with(LocalDensity.current) { 200.dp.toPx() }

                    // Define a cor de fundo do Box condicionalmente
                    val backgroundColor =
                        if (abs(offsetX.value) > 0f) MaterialTheme.colorScheme.error else Color.Transparent

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                            .background(backgroundColor) // Cor de fundo condicional
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        coroutineScope.launch {
                                            if (abs(offsetX.value) > itemWidthPx * 0.3f) {
                                                offsetX.animateTo(
                                                    targetValue = if (offsetX.value > 0) itemWidthPx else -itemWidthPx,
                                                    animationSpec = tween(durationMillis = 200)
                                                )
                                                historyViewModel.deleteRecord(record)
                                            } else {
                                                offsetX.animateTo(0f, animationSpec = spring())
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        coroutineScope.launch {
                                            offsetX.animateTo(0f, animationSpec = spring())
                                        }
                                    }
                                ) { change, dragAmount ->
                                    change.consume()
                                    coroutineScope.launch {
                                        offsetX.snapTo(offsetX.value + dragAmount)
                                    }
                                }
                            }
                    ) {
                        if (offsetX.value < 0) { // Deslize para a esquerda
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar",
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                            )
                        } else if (offsetX.value > 0) { // Deslize para a direita (opcional, se quiser permitir)
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar",
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 16.dp)
                            )
                        }


                        val convertedAmount =
                            unitViewModel.convertMlToSelectedUnit(record.amountMl, selectedUnit)
                        val sign = if (record.amountMl < 0) "-" else ""
                        val itemColor =
                            if (record.amountMl < 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                        val textColor =
                            if (record.amountMl < 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset { IntOffset(offsetX.value.roundToInt(), 0) },
                            colors = CardDefaults.cardColors(containerColor = itemColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${sign}${
                                            String.format(
                                                Locale.getDefault(),
                                                "%.1f",
                                                abs(convertedAmount)
                                            )
                                        } $unitLabel",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Text(
                                        text = record.timestamp.format(
                                            DateTimeFormatter.ofLocalizedDateTime(
                                                FormatStyle.MEDIUM
                                            )
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textColor.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}