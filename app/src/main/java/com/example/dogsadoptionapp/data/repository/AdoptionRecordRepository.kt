package com.example.dogsadoptionapp.data.repository

import androidx.lifecycle.LiveData
import com.example.dogsadoptionapp.data.db.AdoptionRecordDao
import com.example.dogsadoptionapp.data.model.AdoptionRecord
import javax.inject.Inject

class AdoptionRecordRepository @Inject constructor(
    private val dao: AdoptionRecordDao
) {

    suspend fun insert(record: AdoptionRecord) = dao.insert(record)

    fun getAllRecords(): LiveData<List<AdoptionRecord>> = dao.getAllRecords()
}
