package com.example.dogsadoptionapp.data.remoteDB

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRemoteDataSource @Inject constructor(
    private val animalService: AnimalService
) : BaseDataSource (){
    suspend fun getAllDogs() = getResult { animalService.getAllDogs() }
}