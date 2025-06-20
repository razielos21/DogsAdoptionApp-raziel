package com.example.dogsadoptionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stray_reports")
data class StrayReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String?,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)
