package com.gabriel.hydrotrack.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gabriel.hydrotrack.data.local.dao.WaterRecord
import com.gabriel.hydrotrack.data.local.dao.WaterRecordDao

@Database(entities = [WaterRecord::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterRecordDao(): WaterRecordDao
}