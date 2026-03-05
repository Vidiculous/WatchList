package com.watchlist.ui.navigation

sealed class Screen(val route: String) {
    object Watchlist : Screen("watchlist")
    object Services : Screen("services")
    object Settings : Screen("settings")
}
