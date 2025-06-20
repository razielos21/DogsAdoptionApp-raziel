package com.example.dogsadoptionapp.data.model


data class PetfinderResponse (
    val animals: List<Animal>,
    val pagination: Pagination
){
}