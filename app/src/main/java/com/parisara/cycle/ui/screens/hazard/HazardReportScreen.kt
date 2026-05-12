package com.parisara.cycle.ui.screens.hazard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisara.cycle.data.model.HazardCategory
import com.parisara.cycle.ui.theme.HazardRed
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
fun HazardReportScreen(
    onBack: () -> Unit,
    viewModel: HazardViewModel = hiltViewModel()
) {
    val state   by viewModel.state.collectAsState()
    val hazards by viewModel.hazardPins.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var showList by remember { mutableStateOf(false) }
    val context  = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            showForm = false
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Hazard ⚠️") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showList = !showList }) {
                        Icon(Icons.Default.List, "List")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { showForm = true },
                containerColor = HazardRed
            ) {
                Icon(Icons.Default.AddLocation, "Report", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                            overlays.add(myLocation)

                            val events = MapEventsOverlay(
                                object : MapEventsReceiver {
                                    override fun singleTapConfirmedHelper(p: GeoPoint) = false
                                    override fun longPressHelper(p: GeoPoint): Boolean {
                                        viewModel.setPinnedLocation(p)
                                        showForm = true
                                        return true
                                    }
                                }
                            )
                            overlays.add(events)
                        }
                    },
                    update = { map ->
                        map.overlays.removeAll { it is Marker }

                        hazards.forEach { pin ->
                            val cat = runCatching {
                                HazardCategory.valueOf(pin.category)
                            }.getOrDefault(HazardCategory.POTHOLE)

                            val marker = Marker(map).apply {
                                position = GeoPoint(pin.latitude, pin.longitude)
                                title    = "${cat.emoji} ${cat.label}"
                                snippet  = "${pin.description} • 👍 ${pin.upvotes}"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            map.overlays.add(marker)
                        }

                        val pinned = state.pinnedLocation
                        if (pinned != null) {
                            val preview = Marker(map).apply {
                                position = GeoPoint(pinned.latitude, pinned.longitude)
                                title    = "📍 New Report Here"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            map.overlays.add(preview)
                        }

                        map.invalidate()
                    }
                )

                if (hazards.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Text(
                            "📍 Long-press map to report a hazard",
                            modifier = Modifier.padding(12.dp),
                            style    = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (showList && hazards.isNotEmpty()) {
                Surface(
                    modifier        = Modifier.height(220.dp),
                    shadowElevation = 8.dp
                ) {
                    LazyColumn {
                        item {
                            Text(
                                "Community Reports (${hazards.size})",
                                style      = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.padding(16.dp)
                            )
                        }
                        items(hazards) { pin ->
                            val cat = runCatching {
                                HazardCategory.valueOf(pin.category)
                            }.getOrDefault(HazardCategory.POTHOLE)
                            ListItem(
                                headlineContent   = { Text("${cat.emoji} ${cat.label}") },
                                supportingContent = {
                                    Text(pin.description.ifBlank { "No description" })
                                },
                                trailingContent   = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("👍 ${pin.upvotes}")
                                        TextButton(
                                            onClick = { viewModel.upvoteHazard(pin.id) }
                                        ) {
                                            Text(
                                                "Upvote",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        ModalBottomSheet(
            onDismissRequest = {
                showForm = false
                viewModel.resetState()
            }
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Report a Hazard",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.pinnedLocation != null)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text     = if (state.pinnedLocation != null)
                            "📍 Location selected ✅"
                        else
                            "⚠️ Long-press map to select location",
                        modifier = Modifier.padding(12.dp),
                        style    = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text("Category", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HazardCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = state.selectedCategory == cat,
                            onClick  = { viewModel.setCategory(cat) },
                            label    = { Text("${cat.emoji} ${cat.label}") }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value         = state.description,
                    onValueChange = { viewModel.setDescription(it) },
                    label         = { Text("Description (optional)") },
                    minLines      = 2,
                    maxLines      = 3,
                    modifier      = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick  = { viewModel.submitHazard() },
                    enabled  = state.pinnedLocation != null && !state.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = HazardRed)
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Send, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Submit Report")
                    }
                }

                state.error?.let { err ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}