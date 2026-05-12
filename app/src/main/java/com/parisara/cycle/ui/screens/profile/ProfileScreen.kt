package com.parisara.cycle.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.parisara.cycle.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack   : () -> Unit,
    onLogout : () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfilePhoto(it) }
    }

    // Navigate on logout
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) onLogout()
    }

    // Auto-dismiss success message
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearSuccessMessage()
        }
    }

    // Auto-dismiss error message
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearErrorMessage()
        }
    }

    // ─── Logout Dialog ────────────────────────────────────────
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLogoutDialog() },
            icon    = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.error
                )
            },
            title   = { Text("Logout?") },
            text    = {
                Text("Are you sure you want to logout from Parisara-Cycle?")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.logout() },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Logout") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.hideLogoutDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ─── Image Options Bottom Sheet ───────────────────────────
    if (uiState.showImageOptions) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideImageOptions() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text       = "Profile Photo",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(
                        horizontal = 24.dp,
                        vertical   = 16.dp
                    )
                )

                HorizontalDivider()

                // View Photo Option (only if photo exists)
                if (uiState.profile.photoUrl.isNotEmpty()) {
                    ListItem(
                        leadingContent  = {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        },
                        headlineContent = { Text("View Photo") },
                        modifier        = Modifier.clickable {
                            viewModel.hideImageOptions()
                            // Photo is already shown in screen
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Upload / Change Photo
                ListItem(
                    leadingContent  = {
                        Icon(
                            if (uiState.profile.photoUrl.isEmpty())
                                Icons.Default.AddAPhoto
                            else
                                Icons.Default.Edit,
                            contentDescription = null,
                            tint               = GreenPrimary
                        )
                    },
                    headlineContent = {
                        Text(
                            if (uiState.profile.photoUrl.isEmpty())
                                "Add Photo"
                            else
                                "Change Photo"
                        )
                    },
                    supportingContent = {
                        Text(
                            "Choose from gallery",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier.clickable {
                        viewModel.hideImageOptions()
                        imagePickerLauncher.launch("image/*")
                    }
                )

                // Remove Photo (only if photo exists)
                if (uiState.profile.photoUrl.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    ListItem(
                        leadingContent  = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.error
                            )
                        },
                        headlineContent = {
                            Text(
                                "Remove Photo",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier.clickable {
                            viewModel.removeProfilePhoto()
                        }
                    )
                }

                HorizontalDivider()

                // Cancel
                ListItem(
                    leadingContent  = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null
                        )
                    },
                    headlineContent = { Text("Cancel") },
                    modifier        = Modifier.clickable {
                        viewModel.hideImageOptions()
                    }
                )
            }
        }
    }

    // ─── Edit Name Dialog ─────────────────────────────────────
    if (uiState.showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideEditNameDialog() },
            icon    = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint               = GreenPrimary
                )
            },
            title   = { Text("Edit Display Name") },
            text    = {
                Column {
                    Text(
                        text  = "Enter your new display name",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value         = uiState.editNameValue,
                        onValueChange = { viewModel.setEditNameValue(it) },
                        label         = { Text("Display Name") },
                        leadingIcon   = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        isError       = uiState.editNameValue.trim().isEmpty()
                    )
                    if (uiState.editNameValue.trim().isEmpty()) {
                        Text(
                            text  = "Name cannot be empty",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick  = { viewModel.updateDisplayName() },
                    enabled  = uiState.editNameValue.trim().isNotEmpty()
                            && !uiState.isUpdatingName,
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    )
                ) {
                    if (uiState.isUpdatingName) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(16.dp),
                            color       = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.hideEditNameDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ─── Main Scaffold ────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showLogoutDialog() }) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint               = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        // ─── Snackbars ───────────────────────────────────────
        snackbarHost = {
            Column {
                uiState.successMessage?.let { msg ->
                    Snackbar(
                        modifier          = Modifier.padding(16.dp),
                        containerColor    = GreenPrimary,
                        contentColor      = Color.White,
                        dismissAction     = {
                            TextButton(onClick = { viewModel.clearSuccessMessage() }) {
                                Text("OK", color = Color.White)
                            }
                        }
                    ) { Text(msg) }
                }
                uiState.errorMessage?.let { msg ->
                    Snackbar(
                        modifier       = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor   = Color.White,
                        dismissAction  = {
                            TextButton(onClick = { viewModel.clearErrorMessage() }) {
                                Text("OK", color = Color.White)
                            }
                        }
                    ) { Text(msg) }
                }
            }
        }
    ) { padding ->

        if (uiState.isLoading) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement     = Arrangement.spacedBy(16.dp),
            horizontalAlignment     = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(8.dp))

            // ─── Profile Photo Section ────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {

                // Avatar / Photo
                Box(
                    modifier         = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary.copy(alpha = 0.15f))
                        .border(3.dp, GreenPrimary, CircleShape)
                        .clickable { viewModel.showImageOptions() },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isUpdatingPhoto) {
                        CircularProgressIndicator(
                            color    = GreenPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    } else if (uiState.profile.photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model               = uiState.profile.photoUrl,
                            contentDescription  = "Profile Photo",
                            contentScale        = ContentScale.Crop,
                            modifier            = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        // Default avatar with initials
                        Text(
                            text       = uiState.profile.displayName
                                .firstOrNull()
                                ?.uppercaseChar()
                                ?.toString() ?: "C",
                            fontSize   = 42.sp,
                            color      = GreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Edit Photo Button (camera icon)
                SmallFloatingActionButton(
                    onClick            = { viewModel.showImageOptions() },
                    containerColor     = GreenPrimary,
                    contentColor       = Color.White,
                    modifier           = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.CameraAlt,
                        contentDescription = "Edit Photo",
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

            // ─── Name + Edit ──────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text       = uiState.profile.displayName,
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.width(8.dp))
                // Edit Name Button
                IconButton(
                    onClick  = { viewModel.showEditNameDialog() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint               = GreenPrimary,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text  = uiState.profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text  = "🚴 Cyclist since ${uiState.profile.joinedDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // ─── Stats Grid ───────────────────────────────────
            Text(
                text       = "Your Impact",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.align(Alignment.Start)
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Default.Eco,
                    value    = "${uiState.totalCo2}g",
                    label    = "Total CO₂ Saved",
                    color    = GreenPrimary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Default.DirectionsBike,
                    value    = "${uiState.monthlyRides}",
                    label    = "Rides This Month",
                    color    = MaterialTheme.colorScheme.tertiary
                )
            }

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Default.Route,
                    value    = "${String.format("%.1f", uiState.monthlyDistance)}km",
                    label    = "Distance This Month",
                    color    = MaterialTheme.colorScheme.secondary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Default.Park,
                    value    = "${uiState.totalCo2 / 1000}kg",
                    label    = "Total CO₂ (kg)",
                    color    = MaterialTheme.colorScheme.primary
                )
            }

            // ─── Account Info ─────────────────────────────────
            Text(
                text       = "Account Information",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.align(Alignment.Start)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {

                    // Name row with edit button
                    ListItem(
                        leadingContent   = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint               = GreenPrimary
                            )
                        },
                        headlineContent  = {
                            Text(
                                "Display Name",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        supportingContent = {
                            Text(
                                text       = uiState.profile.displayName,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        trailingContent  = {
                            IconButton(
                                onClick = { viewModel.showEditNameDialog() }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Name",
                                    tint               = GreenPrimary,
                                    modifier           = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    ProfileInfoRow(
                        icon  = Icons.Default.Email,
                        label = "Email",
                        value = uiState.profile.email
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    ProfileInfoRow(
                        icon  = Icons.Default.CalendarMonth,
                        label = "Member Since",
                        value = uiState.profile.joinedDate
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    ProfileInfoRow(
                        icon  = Icons.Default.Badge,
                        label = "User ID",
                        value = uiState.profile.uid.take(12) + "..."
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Photo row with edit button
                    ListItem(
                        leadingContent   = {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint               = GreenPrimary
                            )
                        },
                        headlineContent  = {
                            Text(
                                "Profile Photo",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        supportingContent = {
                            Text(
                                text  = if (uiState.profile.photoUrl.isEmpty())
                                    "No photo set"
                                else
                                    "Photo uploaded",
                                fontWeight = FontWeight.Medium
                            )
                        },
                        trailingContent  = {
                            IconButton(
                                onClick = { viewModel.showImageOptions() }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Photo",
                                    tint               = GreenPrimary,
                                    modifier           = Modifier.size(20.dp)
                                )
                            }
                        }
                    )
                }
            }

            // ─── Green Impact ─────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text       = "🌍 Your Green Impact",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("🌳 ${uiState.totalCo2 / 22000} trees worth of CO₂ offset")
                    Text("🚗 ${uiState.totalCo2 / 120}km of car travel avoided")
                    Text("♻️ ${uiState.totalCo2}g total CO₂ saved")
                }
            }

            // ─── App Info ─────────────────────────────────────
            Text(
                text       = "App Settings",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.align(Alignment.Start)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    SettingsRow(
                        icon  = Icons.Default.Info,
                        label = "App Version",
                        value = "v1.0.0"
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingsRow(
                        icon  = Icons.Default.Security,
                        label = "Privacy Policy",
                        value = ""
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingsRow(
                        icon  = Icons.Default.Description,
                        label = "Terms of Service",
                        value = ""
                    )
                }
            }

            // ─── Logout Button ────────────────────────────────
            Button(
                onClick  = { viewModel.showLogoutDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Logout",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text      = "🌿 Every kilometre cycled is a step towards\na greener, healthier town!",
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier  = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Reusable Components ───────────────────────────────────────

@Composable
private fun StatCard(
    modifier : Modifier = Modifier,
    icon     : ImageVector,
    value    : String,
    label    : String,
    color    : Color
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier            = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = color,
                modifier           = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text       = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 20.sp,
                color      = color
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon  : ImageVector,
    label : String,
    value : String
) {
    ListItem(
        leadingContent   = {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = GreenPrimary
            )
        },
        headlineContent  = {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        supportingContent = {
            Text(
                text       = value,
                fontWeight = FontWeight.Medium
            )
        }
    )
}

@Composable
private fun SettingsRow(
    icon  : ImageVector,
    label : String,
    value : String
) {
    ListItem(
        leadingContent   = {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary
            )
        },
        headlineContent  = { Text(label) },
        trailingContent  = {
            if (value.isNotEmpty()) {
                Text(
                    text  = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }
    )
}