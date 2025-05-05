package com.example.dogsadoptionapp.ui.dogslist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dogsadoptionapp.data.db.DogsDatabase
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.data.repository.DogsRepository
import kotlinx.coroutines.launch

class DogsListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DogsRepository
    val allDogs: LiveData<List<Dog>>
    private val dogsCount: Int

    init {
        val dao = DogsDatabase.getDatabase(application).dogsDao()
        repository = DogsRepository(dao)
        allDogs = repository.getAllDogs()
        dogsCount = repository.getAllDogsCount()
    }

    fun deleteDog(dog: Dog) = viewModelScope.launch {
        repository.deleteDog(dog)
    }

    fun insertDog(dog: Dog) = viewModelScope.launch {
        repository.insertDog(dog)
    }

    fun updateDog(dog: Dog) = viewModelScope.launch {
        repository.updateDog(dog)
    }

    fun getDogById(id: Int): LiveData<Dog> {
        return repository.getDogById(id)
    }

    fun deleteAllDogs() {
        viewModelScope.launch {
            repository.deleteAllDogs()
        }
    }
}
