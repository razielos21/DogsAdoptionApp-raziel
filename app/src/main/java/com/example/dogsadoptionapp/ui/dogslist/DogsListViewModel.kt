package com.example.dogsadoptionapp.ui.dogslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.data.repository.DogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogsListViewModel @Inject constructor(
    private val repository: DogsRepository
) : ViewModel() {

    val allDogs: LiveData<List<Dog>> = repository.getAllDogs()

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
