package com.gabriel.hydrotrack

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.gabriel.hydrotrack.navigation.AppNavigation
import com.gabriel.hydrotrack.ui.theme.HydroTrackTheme
import com.gabriel.hydrotrack.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            HydroTrackTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                AppNavigation(navController = navController, themeViewModel = themeViewModel)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes de Hidratação"
            val descriptionText = "Canal para lembretes de ingestão de água"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("water_reminder_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}