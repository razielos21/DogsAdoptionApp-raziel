package com.example.dogsadoptionapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dogsadoptionapp.data.model.AdoptionRecord

@Dao
interface AdoptionRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AdoptionRecord)

    @Query("SELECT * FROM adoption_records ORDER BY date DESC")
    fun getAllRecords(): LiveData<List<AdoptionRecord>>
}
