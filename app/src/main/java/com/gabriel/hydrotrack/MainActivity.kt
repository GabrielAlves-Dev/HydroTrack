package com.gabriel.hydrotrack

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.gabriel.hydrotrack.navigation.AppNavigation
import com.gabriel.hydrotrack.navigation.Screen
import com.gabriel.hydrotrack.presentation.ui.theme.HydroTrackTheme
import com.gabriel.hydrotrack.presentation.viewmodel.ThemeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
            val userEmail = UserPreferencesDataStore(context).loggedInUserEmail.first()
            if (!userEmail.isNullOrBlank()) {
                _startDestination.value = Screen.Home.route
            }
            _isLoading.value = false
        }
    }
}

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    private val mainViewModel: MainViewModel by lazy { MainViewModel(applicationContext) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _userLatitude = MutableStateFlow<Double?>(null)
    private val _userLongitude = MutableStateFlow<Double?>(null)

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.d("MainActivity", "ACCESS_FINE_LOCATION Granted")
                getLastLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d("MainActivity", "ACCESS_COARSE_LOCATION Granted")
                getLastLocation()
            }
            else -> {
                Log.w("MainActivity", "Location permissions denied.")
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun askLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        _userLatitude.value = location.latitude
                        _userLongitude.value = location.longitude
                        Log.d("MainActivity", "Location: Lat ${location.latitude}, Lon ${location.longitude}")
                    } else {
                        Log.w("MainActivity", "Last known location is null.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Error getting location: ${e.message}")
                }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        askNotificationPermission()
        askLocationPermissions()
        createNotificationChannel()

        setContent {
            val isLoading by mainViewModel.isLoading.collectAsState()
            val startDestination by mainViewModel.startDestination.collectAsState()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val navController = rememberNavController()

            val latitude by _userLatitude.collectAsState()
            val longitude by _userLongitude.collectAsState()

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
                            startDestination = startDestination,
                            userLatitude = latitude,
                            userLongitude = longitude
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