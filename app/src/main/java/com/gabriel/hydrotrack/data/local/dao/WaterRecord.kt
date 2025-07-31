package com.gabriel.hydrotrack.data.local.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "water_records")
data class WaterRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val amountMl: Int,
    val timestamp: LocalDateTime
)