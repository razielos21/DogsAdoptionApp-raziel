package com.example.dogsadoptionapp.data.remoteDB

import com.example.dogsadoptionapp.data.model.Animal
import retrofit2.Response
import retrofit2.http.GET

interface AnimalService {

    @GET("animals?type=dog")
    suspend fun getAllDogs() : Response<Animal>
}