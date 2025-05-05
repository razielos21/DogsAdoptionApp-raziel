package com.example.dogsadoptionapp.data.repository

import androidx.lifecycle.LiveData
import com.example.dogsadoptionapp.data.db.DogsDao
import com.example.dogsadoptionapp.data.model.Dog

class DogsRepository( private val dogsDao: DogsDao){

    fun getDogById(id: Int): LiveData<Dog> = dogsDao.getDogById(id)

    fun getAllDogs(): LiveData<List<Dog>> = dogsDao.getAllAvailableDogs()
    
    fun getAllDogsCount(): Int {
        return dogsDao.getAllAvailableDogs().value?.size ?: 0
    }

    suspend fun insertDog(dog: Dog){
        dogsDao.insertDog(dog)
    }

    suspend fun updateDog(dog: Dog){
        dogsDao.updateDog(dog)
    }

    suspend fun deleteDog(dog: Dog){
        dogsDao.deleteDog(dog)

    }

    suspend fun deleteAllDogs(){
        dogsDao.deleteAllDogs()
    }
}
