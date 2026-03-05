package com.watchlist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.watchlist.ui.screen.services.ServicesScreen
import com.watchlist.ui.screen.settings.SettingsScreen
import com.watchlist.ui.screen.watchlist.WatchlistScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Watchlist.route) {
        composable(Screen.Watchlist.route) {
            WatchlistScreen(
                viewModel = hiltViewModel(),
                onNavigateToServices = { navController.navigate(Screen.Services.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Services.route) {
            ServicesScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
