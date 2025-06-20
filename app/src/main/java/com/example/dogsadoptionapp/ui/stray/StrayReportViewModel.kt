package com.example.dogsadoptionapp.ui.stray

import androidx.lifecycle.*
import com.example.dogsadoptionapp.data.model.StrayReport
import com.example.dogsadoptionapp.data.repository.StrayReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StrayReportViewModel @Inject constructor(
    private val repository: StrayReportRepository
) : ViewModel() {

    val allReports: LiveData<List<StrayReport>> = repository.allReports

    fun insert(report: StrayReport) = viewModelScope.launch {
        repository.insert(report)
    }

    fun delete(report: StrayReport) = viewModelScope.launch {
        repository.delete(report)
    }

    fun getReportById(id: Int): LiveData<StrayReport> {
        val result = MutableLiveData<StrayReport>()
        viewModelScope.launch {
            result.postValue(repository.getReportById(id))
        }
        return result
    }
}
