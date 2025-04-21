package com.example.dogsadoptionapp.data.repository

import androidx.lifecycle.LiveData
import com.example.dogsadoptionapp.data.db.AdoptionRecordDao
import com.example.dogsadoptionapp.data.model.AdoptionRecord

class AdoptionRecordRepository(private val dao: AdoptionRecordDao) {

    suspend fun insert(record: AdoptionRecord) = dao.insert(record)

    fun getAllRecords(): LiveData<List<AdoptionRecord>> = dao.getAllRecords()
}
