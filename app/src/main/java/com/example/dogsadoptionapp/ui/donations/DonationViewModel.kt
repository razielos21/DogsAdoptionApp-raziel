package com.example.dogsadoptionapp.ui.donations

import androidx.lifecycle.*
import com.example.dogsadoptionapp.data.model.Donation
import com.example.dogsadoptionapp.data.model.DonationCategory
import com.example.dogsadoptionapp.data.repository.DonationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationViewModel @Inject constructor(
    private val repository: DonationRepository
) : ViewModel() {

    private val _selectedCategory = MutableLiveData<DonationCategory?>()

    val donations: LiveData<List<Donation>> = _selectedCategory.switchMap { category ->
        if (category == null) {
            repository.getAllDonations()
        } else {
            repository.getDonationsByCategory(category)
        }
    }

    fun setCategory(category: DonationCategory?) {
        _selectedCategory.value = category
    }

    fun addDonation(donation: Donation) {
        viewModelScope.launch {
            repository.addDonation(donation)
        }
    }

    fun deleteDonation(donation: Donation) {
        viewModelScope.launch {
            repository.deleteDonation(donation)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAllDonations()
        }
    }
}
