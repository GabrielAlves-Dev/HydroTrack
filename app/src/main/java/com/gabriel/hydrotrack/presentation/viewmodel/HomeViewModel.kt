package com.gabriel.hydrotrack.presentation.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gabriel.hydrotrack.HydroTrackApplication
import com.gabriel.hydrotrack.data.local.dao.WaterRecord
import com.gabriel.hydrotrack.data.local.dao.WaterRecordDao
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.gabriel.hydrotrack.data.remote.weather.WeatherData
import com.gabriel.hydrotrack.data.remote.weather.WeatherRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    application: Application,
    private val latitude: Double?,
    private val longitude: Double?
) : AndroidViewModel(application) {
    private val settingsDataStore = UserPreferencesDataStore(application)
    private val waterRecordDao: WaterRecordDao
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val weatherRepository = WeatherRepository()

    private val _consumedWater = MutableStateFlow(0)
    val consumedWater: StateFlow<Int> = _consumedWater

    val dailyGoal: StateFlow<Int> = settingsDataStore.dailyGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 2000
        )

    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData

    private val _weatherError = MutableStateFlow<String?>(null) // Novo estado para erros do clima
    val weatherError: StateFlow<String?> = _weatherError

    private val _hydrationSuggestion = MutableStateFlow<String?>(null)
    val hydrationSuggestion: StateFlow<String?> = _hydrationSuggestion

    val showWeatherSuggestions: StateFlow<Boolean> = settingsDataStore.showWeatherSuggestions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )


    init {
        waterRecordDao = (application as HydroTrackApplication).database.waterRecordDao()

        viewModelScope.launch {
            combine(
                settingsDataStore.loggedInUserEmail,
                showWeatherSuggestions
            ) { email, showSuggestions ->
                Pair(email, showSuggestions)
            }.collectLatest { (email, showSuggestions) ->
                val uid = firebaseAuth.currentUser?.uid
                if (email != null && uid != null) {
                    settingsDataStore.syncDataFromFirebaseToLocal(email)

                    waterRecordDao.getTotalConsumptionForUserAndDate(uid, LocalDate.now()).collectLatest { totalMl ->
                        _consumedWater.value = totalMl ?: 0
                        settingsDataStore.saveConsumption(_consumedWater.value, LocalDate.now())
                    }

                    if (showSuggestions) {
                        if (latitude != null && longitude != null) {
                            try {
                                val data = weatherRepository.getCurrentWeather(latitude, longitude)
                                _weatherData.value = data
                                _hydrationSuggestion.value = generateHydrationSuggestion(data?.main?.temp)
                                _weatherError.value = null // Limpa o erro em caso de sucesso
                            } catch (e: Exception) {
                                _weatherData.value = null
                                _hydrationSuggestion.value = null
                                _weatherError.value = "Erro ao carregar dados do clima: ${e.localizedMessage ?: "Verifique sua conexão ou permissões."}"
                            }
                        } else {
                            _weatherData.value = null
                            _hydrationSuggestion.value = null
                            _weatherError.value = "Localização não disponível para sugestões de clima." // Erro se lat/lon forem nulos
                        }
                    } else {
                        _weatherData.value = null
                        _hydrationSuggestion.value = null
                        _weatherError.value = null // Limpa o erro se a opção estiver desativada
                    }
                } else {
                    _consumedWater.value = 0
                    _weatherData.value = null
                    _hydrationSuggestion.value = null
                    _weatherError.value = null
                }
            }
        }
        checkDateAndResetConsumption()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDateAndResetConsumption() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val lastDate = settingsDataStore.lastConsumptionDate.first()

            if (today != lastDate) {
                settingsDataStore.saveConsumption(0, LocalDate.now())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addWater(amountMl: Int) {
        val uid = firebaseAuth.currentUser?.uid
        if (amountMl > 0 && uid != null) {
            viewModelScope.launch {
                val newRecord = WaterRecord(
                    userId = uid,
                    amountMl = amountMl,
                    timestamp = LocalDateTime.now()
                )
                waterRecordDao.insert(newRecord)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeWater(amountMl: Int) {
        val uid = firebaseAuth.currentUser?.uid
        if (amountMl > 0 && uid != null) {
            viewModelScope.launch {
                val newRecord = WaterRecord(
                    userId = uid,
                    amountMl = -amountMl,
                    timestamp = LocalDateTime.now()
                )
                waterRecordDao.insert(newRecord)
            }
        }
    }

    private fun generateHydrationSuggestion(temperature: Double?): String {
        return temperature?.let { temp ->
            when {
                temp >= 30.0 -> "Está muito quente! Beba bastante água para se manter hidratado. Considere aumentar sua meta diária!"
                temp >= 25.0 -> "O clima está quente. Mantenha-se hidratado e reponha os líquidos perdidos."
                temp >= 20.0 -> "Temperatura agradável, mas não esqueça de beber água regularmente."
                temp >= 10.0 -> "Clima ameno. Continue com sua hidratação diária."
                else -> "Temperatura mais baixa. Lembre-se que a hidratação ainda é importante!"
            }
        } ?: "Informações climáticas não disponíveis para sugestões de hidratação."
    }


    companion object {
        class HomeViewModelFactory(
            private val application: Application,
            private val latitude: Double?,
            private val longitude: Double?
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return HomeViewModel(application, latitude, longitude) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}