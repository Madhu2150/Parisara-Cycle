package com.parisara.cycle.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisara.cycle.data.model.HazardCategory
import com.parisara.cycle.ui.theme.GreenPrimary
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    var showAiSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue   = context.packageName
            osmdroidTileCache = java.io.File(context.cacheDir, "osmdroid")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safe Route Map 🗺️") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleHazards() }) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Toggle Hazards",
                            tint = if (uiState.showHazards)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (uiState.aiSummary.isNotBlank()) {
                        IconButton(onClick = { showAiSheet = true }) {
                            Icon(Icons.Default.AutoAwesome, "AI Summary")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory  = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(12.9716, 77.5946))

                        val locationOverlay = MyLocationNewOverlay(
                            GpsMyLocationProvider(ctx), this
                        )
                        locationOverlay.enableMyLocation()
                        locationOverlay.enableFollowLocation()
                        overlays.add(locationOverlay)

                        val eventsOverlay = MapEventsOverlay(
                            object : MapEventsReceiver {
                                override fun singleTapConfirmedHelper(p: GeoPoint) = false
                                override fun longPressHelper(p: GeoPoint): Boolean {
                                    viewModel.setDestination(p)
                                    return true
                                }
                            }
                        )
                        overlays.add(eventsOverlay)
                    }
                },
                update = { map ->
                    map.overlays.removeAll { it is Marker }

                    if (uiState.showHazards) {
                        uiState.hazardPins.forEach { pin ->
                            val cat = runCatching {
                                HazardCategory.valueOf(pin.category)
                            }.getOrDefault(HazardCategory.POTHOLE)

                            val marker = Marker(map).apply {
                                position = GeoPoint(pin.latitude, pin.longitude)
                                title    = "${cat.emoji} ${cat.label}"
                                snippet  = pin.description.ifBlank { "Community report" }
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            map.overlays.add(marker)
                        }
                    }

                    val dest = uiState.destination
                    if (dest != null) {
                        val destMarker = Marker(map).apply {
                            position = GeoPoint(dest.latitude, dest.longitude)
                            title    = "📍 Destination"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        map.overlays.add(destMarker)
                    }

                    map.invalidate()
                }
            )

            // Map Legend
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Hazard Map",
                        style      = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("🕳️ Pothole",           style = MaterialTheme.typography.bodySmall)
                    Text("🚧 Blocked Path",       style = MaterialTheme.typography.bodySmall)
                    Text("⚠️ Dangerous Junction", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${uiState.hazardPins.size} reports",
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenPrimary
                    )
                }
            }

            // Error
            val errMsg = uiState.errorMessage
            if (errMsg != null) {
                LaunchedEffect(errMsg) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearError()
                }
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                ) { Text(errMsg) }
            }
        }
    }

    // AI Summary Sheet
    if (showAiSheet) {
        ModalBottomSheet(onDismissRequest = { showAiSheet = false }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = GreenPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "AI Safety Summary",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text  = uiState.aiSummary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}