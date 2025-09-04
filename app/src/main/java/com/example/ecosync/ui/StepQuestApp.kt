package com.example.ecosync.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ecosync.ui.screens.EnergyScreen
import com.example.ecosync.ui.screens.HomeScreen
import com.example.ecosync.ui.screens.JourneyScreen
import com.example.ecosync.ui.screens.ProfileScreen
import com.example.ecosync.ui.screens.RewardsScreen
import com.example.ecosync.ui.screens.ScanScreen
import com.example.ecosync.ui.screens.CommunityScreen

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    data object Home : Screen("home", "Home", { Icon(Icons.Filled.Home, contentDescription = null) })
    data object Journey : Screen("journey", "Journey", { Icon(Icons.Filled.Flight, contentDescription = null) })
    data object Community : Screen("community", "Community", { Icon(Icons.Filled.Groups, contentDescription = null) })
    data object Rewards : Screen("rewards", "Rewards", { Icon(Icons.Filled.CardGiftcard, contentDescription = null) })
    data object Profile : Screen("profile", "Profile", { Icon(Icons.Filled.Person, contentDescription = null) })
    data object Energy : Screen("energy", "Energy", { Icon(Icons.Filled.Bolt, contentDescription = null) })
    data object Scan : Screen("scan", "Scan", { Icon(Icons.Filled.QrCodeScanner, contentDescription = null) })
}

@Composable
fun EcoSyncApp() {
    val navController = rememberNavController()

    // Keep 5 items for a tidy bottom bar. Energy remains a route (see NavHost) but not on the bar.
    val items = listOf(
        Screen.Journey,
        Screen.Community,   // <-- new tab
        Screen.Rewards,
        Screen.Home,
        Screen.Scan,
        Screen.Energy,
        Screen.Profile
        // If you prefer, you can include Screen.Scan here instead of one of the above.
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val destination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        selected = destination.isTopLevel(screen.route),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { screen.icon() },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Journey.route) { JourneyScreen() }
            composable(Screen.Community.route) { CommunityScreen() }   // <-- new route
            composable(Screen.Rewards.route) { RewardsScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.Energy.route) { EnergyScreen() }         // use sealed-route, not hard-coded string
            composable(Screen.Scan.route) { ScanScreen() }
        }
    }
}

private fun NavDestination?.isTopLevel(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}