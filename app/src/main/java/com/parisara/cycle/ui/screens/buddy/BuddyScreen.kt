package com.parisara.cycle.ui.screens.buddy

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisara.cycle.ui.theme.GreenPrimary
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuddyScreen(
    onBack: () -> Unit,
    viewModel: BuddyViewModel = hiltViewModel()
) {
    val state   by viewModel.uiState.collectAsState()
    var routeInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buddy System 👥") },
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
                .padding(padding)
        ) {
            // OSM Map with buddy markers
            Box(modifier = Modifier.weight(1f)) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory  = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(15.0)
                            controller.setCenter(GeoPoint(12.9716, 77.5946))

                            val myLocation = MyLocationNewOverlay(
                                GpsMyLocationProvider(ctx), this
                            )
                            myLocation.enableMyLocation()
                            myLocation.enableFollowLocation()
                            overlays.add(myLocation)
                        }
                    },
                    update = { map ->
                        // Remove old buddy markers
                        map.overlays.removeAll { it is Marker }

                        // Add buddy markers
                        state.buddies.forEach { buddy ->
                            val marker = Marker(map).apply {
                                position = GeoPoint(buddy.latitude, buddy.longitude)
                                title    = "🚴 ${buddy.displayName}"
                                snippet  = "Buddy on your route"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            map.overlays.add(marker)
                        }
                        map.invalidate()
                    }
                )

                // Buddy count badge
                if (state.isBuddyModeActive) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenPrimary)
                    ) {
                        Text(
                            "👥 ${state.buddies.size} nearby",
                            color    = Color.White,
                            modifier = Modifier.padding(8.dp),
                            style    = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Controls
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!state.isBuddyModeActive) {
                        OutlinedTextField(
                            value         = routeInput,
                            onValueChange = { routeInput = it },
                            label         = { Text("Route ID") },
                            placeholder   = { Text("e.g., college-route-1") },
                            leadingIcon   = { Icon(Icons.Default.Route, null) },
                            singleLine    = true,
                            modifier      = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick  = {
                                val routeId = routeInput.ifBlank {
                                    "route-${UUID.randomUUID().toString().take(6)}"
                                }
                                viewModel.startBuddyMode(routeId)
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary
                            )
                        ) {
                            Icon(Icons.Default.PersonSearch, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Start Buddy Mode", fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "📡 Broadcasts location every 10 seconds",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("🟢 Buddy Mode Active", fontWeight = FontWeight.Bold)
                                    Text(
                                        "Route: ${state.currentRouteId}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "${state.buddies.size} buddy/buddies found",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GreenPrimary
                                    )
                                }
                                Icon(Icons.Default.Radar, null, tint = GreenPrimary)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick  = { viewModel.stopBuddyMode() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Stop, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Stop Buddy Mode")
                        }
                    }
                }
            }
        }
    }
}
