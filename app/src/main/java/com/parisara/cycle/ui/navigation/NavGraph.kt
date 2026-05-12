package com.parisara.cycle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.parisara.cycle.ui.screens.auth.AuthScreen
import com.parisara.cycle.ui.screens.auth.AuthViewModel
import com.parisara.cycle.ui.screens.buddy.BuddyScreen
import com.parisara.cycle.ui.screens.ecostats.EcoStatsScreen
import com.parisara.cycle.ui.screens.hazard.HazardReportScreen
import com.parisara.cycle.ui.screens.home.HomeScreen
import com.parisara.cycle.ui.screens.map.MapScreen
import com.parisara.cycle.ui.screens.profile.ProfileScreen

object Routes {
    const val AUTH      = "auth"
    const val HOME      = "home"
    const val MAP       = "map"
    const val HAZARD    = "hazard"
    const val ECO_STATS = "eco_stats"
    const val BUDDY     = "buddy"
    const val PROFILE   = "profile"   // ✅ New
}

@Composable
fun ParisaraNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val startDestination = if (authViewModel.currentUser != null)
        Routes.HOME
    else
        Routes.AUTH

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToMap      = { navController.navigate(Routes.MAP) },
                onNavigateToEcoStats = { navController.navigate(Routes.ECO_STATS) },
                onNavigateToBuddy    = { navController.navigate(Routes.BUDDY) },
                onNavigateToHazard   = { navController.navigate(Routes.HAZARD) },
                onNavigateToProfile  = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.MAP) {
            MapScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.HAZARD) {
            HazardReportScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ECO_STATS) {
            EcoStatsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.BUDDY) {
            BuddyScreen(onBack = { navController.popBackStack() })
        }

        // ✅ New Profile Route
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack   = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}