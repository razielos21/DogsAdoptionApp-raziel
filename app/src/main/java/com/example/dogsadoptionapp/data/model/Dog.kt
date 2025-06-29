package com.example.dogsadoptionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dogs")
data class Dog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val age: Int = 0,
    val breed: String = "",
    val gender: String = "",
    val imageUri: String = "",
    val isFavorite: Boolean = false,
    val isAdopted: Boolean = false
)