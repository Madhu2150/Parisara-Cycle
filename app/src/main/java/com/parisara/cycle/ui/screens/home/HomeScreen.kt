package com.parisara.cycle.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisara.cycle.ui.screens.ecostats.EcoStatsViewModel
import com.parisara.cycle.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMap      : () -> Unit,
    onNavigateToEcoStats : () -> Unit,
    onNavigateToBuddy    : () -> Unit,
    onNavigateToHazard   : () -> Unit,
    onNavigateToProfile  : () -> Unit,   // ✅ New
    ecoViewModel         : EcoStatsViewModel = hiltViewModel()
) {
    val ecoState by ecoViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint               = GreenPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Parisara-Cycle",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // ✅ Profile button in top bar
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector        = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint               = GreenPrimary,
                            modifier           = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            // CO2 Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = GreenPrimary),
                shape    = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🌿 Today's Green Impact",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${ecoState.dailyCo2Grams}g",
                        color      = Color.White,
                        fontSize   = 48.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "CO₂ saved today",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.3f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        EcoStatItem(
                            value = "${ecoState.monthlyCo2Grams}g",
                            label = "This Month"
                        )
                        EcoStatItem(
                            value = String.format("%.1f", ecoState.monthlyDistanceKm) + "km",
                            label = "Distance"
                        )
                        EcoStatItem(
                            value = "${ecoState.monthlyRideCount}",
                            label = "Rides"
                        )
                    }
                }
            }

            // Quick Actions
            Text(
                "Quick Actions",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier       = Modifier.weight(1f),
                    icon           = Icons.Default.Map,
                    label          = "Safe Route",
                    description    = "Navigate safely",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick        = onNavigateToMap
                )
                QuickActionCard(
                    modifier       = Modifier.weight(1f),
                    icon           = Icons.Default.Warning,
                    label          = "Report Hazard",
                    description    = "Help community",
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    onClick        = onNavigateToHazard
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier       = Modifier.weight(1f),
                    icon           = Icons.Default.People,
                    label          = "Find Buddy",
                    description    = "Cycle together",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick        = onNavigateToBuddy
                )
                QuickActionCard(
                    modifier       = Modifier.weight(1f),
                    icon           = Icons.Default.Eco,
                    label          = "Eco Stats",
                    description    = "Track savings",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick        = onNavigateToEcoStats
                )
            }

            // ✅ Profile Quick Access Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick  = onNavigateToProfile,
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector        = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint               = GreenPrimary,
                        modifier           = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "My Profile",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "View stats, settings & logout",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }
            }

            // Mission card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier          = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🚴", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Every km cycled is a step towards",
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "a greener, healthier town! 🌱",
                            style      = MaterialTheme.typography.bodyMedium,
                            color      = GreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ─── Helper Composables ────────────────────────────────────────

@Composable
private fun EcoStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            color      = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize   = 20.sp
        )
        Text(
            label,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun QuickActionCard(
    modifier       : Modifier = Modifier,
    icon           : ImageVector,
    label          : String,
    description    : String,
    containerColor : Color,
    onClick        : () -> Unit
) {
    Card(
        modifier = modifier,
        onClick  = onClick,
        colors   = CardDefaults.cardColors(containerColor = containerColor),
        shape    = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                icon,
                contentDescription = label,
                modifier           = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}