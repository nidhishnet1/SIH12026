package com.example.demosih11.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lots")
data class Lot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val weight: Double,
    val estimatedValue: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, HANDED_OVER
    val photoPath: String? = null
)