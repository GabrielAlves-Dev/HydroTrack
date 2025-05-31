package com.gabriel.hydrotrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabriel.hydrotrack.ui.home.HomeScreen
import com.gabriel.hydrotrack.ui.screens.goal.GoalScreen
import com.gabriel.hydrotrack.ui.screens.login.LoginScreen
import com.gabriel.hydrotrack.ui.screens.profile.ProfileScreen
import com.gabriel.hydrotrack.ui.screens.settings.SettingsScreen
import com.gabriel.hydrotrack.ui.screens.unit.UnitScreen
import com.gabriel.hydrotrack.viewmodel.GoalViewModel
import com.gabriel.hydrotrack.viewmodel.HomeViewModel
import com.gabriel.hydrotrack.viewmodel.ThemeViewModel
import com.gabriel.hydrotrack.viewmodel.UnitViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Goal : Screen("goal")
    object Unit : Screen("unit")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {

            HomeScreen(
                navController = navController,
                themeViewModel = themeViewModel,
                homeViewModel = viewModel<HomeViewModel>()
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.Goal.route) {

            GoalScreen(
                navController = navController,
                goalViewModel = viewModel<GoalViewModel>()
            )
        }
        composable(Screen.Unit.route) {

            UnitScreen(
                navController = navController,
                unitViewModel = viewModel<UnitViewModel>()
            )
        }
    }
}