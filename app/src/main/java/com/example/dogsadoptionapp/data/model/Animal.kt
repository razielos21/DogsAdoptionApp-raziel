package com.example.dogsadoptionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Animal(  @PrimaryKey
                    val id: Long,
                    val organization_id: String,
                    val type: String,
                    val breeds: Breeds,
                    val age: String,
                    val gender: String,
                    val size: String,
                    val tags: List<String>,
                    val name: String,
                    val description: String?,
                    val photos: List<Photo>,
                    val status: String,
) {

}