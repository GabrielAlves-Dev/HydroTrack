package com.gabriel.hydrotrack.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gabriel.hydrotrack.R
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.gabriel.hydrotrack.presentation.viewmodel.WaterUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        if (currentHour < 6 || currentHour > 18) {
            return
        }

        val dataStore = UserPreferencesDataStore(context)
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dailyGoal = dataStore.dailyGoal.first()
                val dailyConsumption = dataStore.dailyConsumption.first()
                val unitOrdinal = dataStore.waterUnit.first()
                val selectedUnit = WaterUnit.values().getOrElse(unitOrdinal) { WaterUnit.ML }

                val remaining = dailyGoal - dailyConsumption
                if (remaining > 0) {
                    val convertedRemaining = remaining / selectedUnit.mlEquivalent
                    val unitLabel = when(selectedUnit) {
                        WaterUnit.ML -> "ml"
                        WaterUnit.LITERS -> "L"
                        else -> selectedUnit.displayName.substringBefore(" (").trim().lowercase()
                    }

                    val notificationText = String.format(
                        "Faltam %.1f %s para você bater sua meta. Vamos lá!",
                        convertedRemaining,
                        unitLabel
                    )
                    showNotification(context, notificationText)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, text: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "hydration_reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("HydroTrack Lembrete")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}