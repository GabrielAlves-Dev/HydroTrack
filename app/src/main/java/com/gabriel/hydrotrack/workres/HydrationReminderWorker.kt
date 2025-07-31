package com.gabriel.hydrotrack.workers

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gabriel.hydrotrack.R
import com.gabriel.hydrotrack.data.local.preferences.UserPreferencesDataStore
import com.gabriel.hydrotrack.presentation.viewmodel.WaterUnit
import kotlinx.coroutines.flow.first
import java.util.Calendar

class HydrationReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        
        if (currentHour < 6 || currentHour >= 18) {
            return Result.success()
        }

        val dataStore = UserPreferencesDataStore(applicationContext)

        return try {
            val dailyGoal = dataStore.dailyGoal.first()
            val dailyConsumption = dataStore.dailyConsumption.first()
            val unitOrdinal = dataStore.waterUnit.first()
            val selectedUnit = WaterUnit.values().getOrElse(unitOrdinal) { WaterUnit.ML }

            val remaining = dailyGoal - dailyConsumption
            if (remaining > 0) {
                val convertedRemaining = remaining / selectedUnit.mlEquivalent
                val unitLabel = when (selectedUnit) {
                    WaterUnit.ML -> "ml"
                    WaterUnit.LITERS -> "L"
                    else -> selectedUnit.displayName.substringBefore(" (").trim().lowercase()
                }

                val notificationText = String.format(
                    "Faltam %.1f %s para você bater sua meta. Vamos lá!",
                    convertedRemaining,
                    unitLabel
                )
                showNotification(applicationContext, notificationText)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
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