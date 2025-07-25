package com.gabriel.hydrotrack.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gabriel.hydrotrack.data.dao.WaterRecordDao
import com.gabriel.hydrotrack.data.model.WaterRecord
import com.gabriel.hydrotrack.data.converters.Converters

@Database(entities = [WaterRecord::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterRecordDao(): WaterRecordDao
}