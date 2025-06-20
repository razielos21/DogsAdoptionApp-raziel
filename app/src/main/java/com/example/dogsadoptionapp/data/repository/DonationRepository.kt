package com.example.dogsadoptionapp.data.repository

import androidx.lifecycle.LiveData
import com.example.dogsadoptionapp.data.db.DonationDao
import com.example.dogsadoptionapp.data.model.Donation
import com.example.dogsadoptionapp.data.model.DonationCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonationRepository @Inject constructor(
    private val donationDao: DonationDao
) {

    fun getAllDonations(): LiveData<List<Donation>> =
        donationDao.getAllDonations()

    fun getDonationsByCategory(category: DonationCategory): LiveData<List<Donation>> =
        donationDao.getDonationsByCategory(category)

    suspend fun addDonation(donation: Donation) {
        donationDao.insertDonation(donation)
    }

    suspend fun deleteDonation(donation: Donation) {
        donationDao.deleteDonation(donation)
    }

    suspend fun deleteAllDonations() {
        donationDao.deleteAllDonations()
    }
}
