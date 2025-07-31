package com.gabriel.hydrotrack.service

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gabriel.hydrotrack.workers.HydrationReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleRepeatingNotifications() {
        val initialDelay = calculateInitialDelay()

        val hydrationWorkRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            repeatInterval = 3,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("hydration_reminder_work")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "HydrationReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            hydrationWorkRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val currentTime = Calendar.getInstance()
        val nextScheduledTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (currentTime.after(nextScheduledTime)) {
            nextScheduledTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        return nextScheduledTime.timeInMillis - currentTime.timeInMillis
    }


    fun cancelNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork("HydrationReminder")
    }
}