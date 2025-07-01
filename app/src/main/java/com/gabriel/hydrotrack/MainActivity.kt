package com.gabriel.hydrotrack

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.gabriel.hydrotrack.data.SettingsDataStore
import com.gabriel.hydrotrack.navigation.AppNavigation
import com.gabriel.hydrotrack.navigation.Screen
import com.gabriel.hydrotrack.ui.theme.HydroTrackTheme
import com.gabriel.hydrotrack.viewmodel.ThemeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow(Screen.Login.route)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val user = SettingsDataStore(context).loggedInUserEmail.first()
            if (!user.isNullOrBlank()) {
                _startDestination.value = Screen.Home.route
            }
            _isLoading.value = false
        }
    }
}


class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    private val mainViewModel: MainViewModel by lazy { MainViewModel(applicationContext) }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        askNotificationPermission()
        createNotificationChannel()

        setContent {
            val isLoading by mainViewModel.isLoading.collectAsState()
            val startDestination by mainViewModel.startDestination.collectAsState()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val navController = rememberNavController()

            HydroTrackTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        AppNavigation(
                            navController = navController,
                            themeViewModel = themeViewModel,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes de Hidratação"
            val descriptionText = "Canal para lembretes de ingestão de água"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("hydration_reminder_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}