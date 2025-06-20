package com.example.dogsadoptionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class Donation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val donorName: String,
    val category: DonationCategory,
    val description: String,
    val date: String
)

enum class DonationCategory {
    FOOD, TOYS, EQUIPMENT
}
