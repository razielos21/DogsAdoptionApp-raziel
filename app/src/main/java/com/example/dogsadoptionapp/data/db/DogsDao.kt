package com.example.dogsadoptionapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dogsadoptionapp.data.model.Dog

@Dao
interface DogsDao {

    @Query("SELECT * FROM dogs WHERE isAdopted = 0")
    fun getAllAvailableDogs(): LiveData<List<Dog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDog(dog: Dog)

    @Update
    suspend fun updateDog(dog: Dog)

    @Delete
    suspend fun deleteDog(dog: Dog)

    @Query("SELECT * FROM dogs WHERE id = :id")
    fun getDogById(id: Int): LiveData<Dog>
}
