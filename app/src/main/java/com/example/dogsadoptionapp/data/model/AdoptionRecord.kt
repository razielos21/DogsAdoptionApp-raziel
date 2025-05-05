package com.example.dogsadoptionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adoption_records")
data class AdoptionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dogId: Int = -1,
    val dogName: String = "",
    val dogImageUri: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val city: String = "",
    val date: String = "",
    val phone: String = "",
    val email: String = ""
)

