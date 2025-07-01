package com.gabriel.hydrotrack.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gabriel.hydrotrack.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Cria a notificação
        val notification = NotificationCompat.Builder(context, "water_reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use um ícone apropriado
            .setContentTitle("HydroTrack Lembrete")
            .setContentText("Está na hora de se hidratar. Beba um copo de água!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Envia a notificação com um ID único
        notificationManager.notify(1, notification)
    }
}