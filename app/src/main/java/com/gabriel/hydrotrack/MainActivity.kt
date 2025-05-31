package com.gabriel.hydrotrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.gabriel.hydrotrack.navigation.AppNavigation
import com.gabriel.hydrotrack.ui.theme.HydroTrackTheme
import com.gabriel.hydrotrack.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HydroTrackTheme(darkTheme = themeViewModel.isDarkTheme) {
                val navController = rememberNavController()
                AppNavigation(navController = navController, themeViewModel = themeViewModel)
            }
        }
    }
}
