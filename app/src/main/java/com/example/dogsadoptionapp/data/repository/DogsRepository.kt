package com.example.dogsadoptionapp.data.repository

import androidx.lifecycle.LiveData
import com.example.dogsadoptionapp.data.db.DogsDao
import com.example.dogsadoptionapp.data.model.Dog

class DogsRepository(private val dogsDao: DogsDao) {

    fun getAllDogs(): LiveData<List<Dog>> = dogsDao.getAllAvailableDogs()

    suspend fun insertDog(dog: Dog) = dogsDao.insertDog(dog)

    suspend fun updateDog(dog: Dog) = dogsDao.updateDog(dog)

    suspend fun deleteDog(dog: Dog) = dogsDao.deleteDog(dog)

    fun getDogById(id: Int): LiveData<Dog> = dogsDao.getDogById(id)
}
