package com.gabriel.hydrotrack

import android.app.Application
import com.gabriel.hydrotrack.data.db.AppDatabase
import androidx.room.Room

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