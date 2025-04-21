package com.example.dogsadoptionapp.ui.adoption

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dogsadoptionapp.data.db.DogsDatabase
import com.example.dogsadoptionapp.data.model.AdoptionRecord
import com.example.dogsadoptionapp.data.repository.AdoptionRecordRepository
import kotlinx.coroutines.launch

class AdoptionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AdoptionRecordRepository
    val allRecords: LiveData<List<AdoptionRecord>>

    init {
        val dao = DogsDatabase.getDatabase(application).adoptionRecordDao()
        repository = AdoptionRecordRepository(dao)
        allRecords = repository.getAllRecords()
    }

    fun insert(record: AdoptionRecord) = viewModelScope.launch {
        repository.insert(record)
    }
}
