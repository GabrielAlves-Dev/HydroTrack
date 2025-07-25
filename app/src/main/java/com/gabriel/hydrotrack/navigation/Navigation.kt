package com.gabriel.hydrotrack.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gabriel.hydrotrack.ui.home.HomeScreen
import com.gabriel.hydrotrack.ui.screens.goal.GoalScreen
import com.gabriel.hydrotrack.ui.screens.login.LoginScreen
import com.gabriel.hydrotrack.ui.screens.profile.ProfileScreen
import com.gabriel.hydrotrack.ui.screens.settings.SettingsScreen
import com.gabriel.hydrotrack.ui.screens.unit.UnitScreen
import com.gabriel.hydrotrack.ui.screens.history.ConsumptionHistoryScreen
import com.gabriel.hydrotrack.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Goal : Screen("goal")
    object Unit : Screen("unit")
    object ConsumptionHistory : Screen("consumption_history")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                loginViewModel = viewModel()
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                themeViewModel = themeViewModel,
                homeViewModel = viewModel<HomeViewModel>(),
                unitViewModel = viewModel<UnitViewModel>()
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                profileViewModel = viewModel<ProfileViewModel>()
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.Goal.route) {
            GoalScreen(
                navController = navController,
                goalViewModel = viewModel<GoalViewModel>(),
                unitViewModel = viewModel<UnitViewModel>()
            )
        }
        composable(Screen.Unit.route) {
            UnitScreen(
                navController = navController,
                unitViewModel = viewModel<UnitViewModel>()
            )
        }
        composable(Screen.ConsumptionHistory.route) {
            ConsumptionHistoryScreen(
                navController = navController,
                historyViewModel = viewModel<ConsumptionHistoryViewModel>()
            )
        }
    }
}