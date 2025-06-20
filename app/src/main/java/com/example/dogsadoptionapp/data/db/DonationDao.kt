package com.example.dogsadoptionapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dogsadoptionapp.data.model.Donation
import com.example.dogsadoptionapp.data.model.DonationCategory

@Dao
interface DonationDao {

    @Query("SELECT * FROM donations ORDER BY date DESC")
    fun getAllDonations(): LiveData<List<Donation>>

    @Query("SELECT * FROM donations WHERE category = :category ORDER BY date DESC")
    fun getDonationsByCategory(category: DonationCategory): LiveData<List<Donation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: Donation)

    @Delete
    suspend fun deleteDonation(donation: Donation)

    @Query("DELETE FROM donations")
    suspend fun deleteAllDonations()
}
