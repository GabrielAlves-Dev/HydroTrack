package com.gabriel.hydrotrack

import android.app.Application
import androidx.room.Room
import com.gabriel.hydrotrack.data.local.db.AppDatabase

class HydroTrackApplication : Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "hydrotrack_database"
        ).build()
    }
}