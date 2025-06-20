package com.example.dogsadoptionapp.ui.adoption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import com.example.dogsadoptionapp.data.model.AdoptionRecord
import com.example.dogsadoptionapp.data.repository.AdoptionRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdoptionViewModel @Inject constructor(
    private val repository: AdoptionRecordRepository
) : ViewModel() {

    val allRecords: LiveData<List<AdoptionRecord>> = repository.getAllRecords()

    fun insert(record: AdoptionRecord) = viewModelScope.launch {
        repository.insert(record)
    }
}
