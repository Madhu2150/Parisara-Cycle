package com.parisara.cycle.ui.screens.ecostats

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisara.cycle.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoStatsScreen(
    onBack: () -> Unit,
    viewModel: EcoStatsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Animated CO2 value
    val animatedMonthly by animateIntAsState(
        targetValue = state.monthlyCo2Grams,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "monthly_co2"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eco Stats") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Monthly CO2 Gauge Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(20.dp),
                colors   = CardDefaults.cardColors(containerColor = GreenPrimary)
            ) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Monthly CO₂ Savings", color = Color.White,
                        style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))

                    // Circular progress arc
                    val progress = (state.monthlyCo2Grams / 5000f).coerceIn(0f, 1f)
                    Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(160.dp)) {
                            val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            // Background arc
                            drawArc(
                                color      = Color.White.copy(alpha = 0.2f),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter  = false,
                                style      = stroke,
                                topLeft    = Offset(stroke.width / 2, stroke.width / 2),
                                size       = Size(
                                    size.width - stroke.width,
                                    size.height - stroke.width
                                )
                            )
                            // Progress arc
                            drawArc(
                                color      = Color.White,
                                startAngle = 135f,
                                sweepAngle = 270f * progress,
                                useCenter  = false,
                                style      = stroke,
                                topLeft    = Offset(stroke.width / 2, stroke.width / 2),
                                size       = Size(
                                    size.width - stroke.width,
                                    size.height - stroke.width
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${animatedMonthly}g",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text("CO₂ saved", color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Stats Grid
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value    = "${state.dailyCo2Grams}g",
                    label    = "Today",
                    icon     = "☀️"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value    = "${state.monthlyRideCount}",
                    label    = "Rides",
                    icon     = "🚴"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value    = "${String.format("%.1f", state.monthlyDistanceKm)}km",
                    label    = "Distance",
                    icon     = "📍"
                )
            }

            // Total Savings
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total CO₂ Saved", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                        Text("All time", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "${state.totalCo2Grams}g",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = GreenPrimary
                    )
                }
            }

            // Equivalent Impact
            val trees = state.monthlyCo2Grams / 22000  // ~22kg CO2 per tree per year
            val carKm = state.monthlyCo2Grams / 120
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Impact = ", style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("🌳 Equivalent to $trees trees planted this month")
                    Text("🚗 ${carKm}km of car travel avoided")
                }
            }

            // Manual Ride Logger
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Log a Ride", style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value         = state.inputDistanceKm,
                            onValueChange = { viewModel.setInputDistance(it) },
                            label         = { Text("Distance (km)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine  = true,
                            modifier    = Modifier.weight(1f)
                        )
                        Button(
                            onClick  = { viewModel.logManualRide() },
                            enabled  = state.inputDistanceKm.isNotBlank()
                        ) {
                            Text("Log")
                        }
                    }
                    state.inputDistanceKm.toDoubleOrNull()?.let { km ->
                        if (km > 0) {
                            Text(
                                "= ${(km * 120).toInt()}g CO₂ saved",
                                style = MaterialTheme.typography.bodySmall,
                                color = GreenPrimary
                            )
                        }
                    }
                }
            }

            // Recent Rides
            if (state.recentRides.isNotEmpty()) {
                Text("Recent Rides", style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold)
                state.recentRides.forEach { ride ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = {
                                Text("${String.format("%.1f", ride.distanceKm)} km")
                            },
                            supportingContent = { Text(ride.dateKey) },
                            trailingContent   = {
                                Text(
                                    "+${ride.co2SavedGrams}g CO₂",
                                    color = GreenPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingContent = { Text("🚴", fontSize = 24.sp) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: String
) {
    Card(modifier = modifier) {
        Column(
            modifier            = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Text(value, fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleMedium)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}