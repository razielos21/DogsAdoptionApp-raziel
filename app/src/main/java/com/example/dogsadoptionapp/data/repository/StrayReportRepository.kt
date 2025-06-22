package com.example.dogsadoptionapp.data.repository

import androidx.lifecycle.LiveData
import com.example.dogsadoptionapp.data.db.StrayReportDao
import com.example.dogsadoptionapp.data.model.StrayReport
import javax.inject.Inject

class StrayReportRepository @Inject constructor(
    private val dao: StrayReportDao
) {

    val allReports: LiveData<List<StrayReport>> = dao.getAllReports()

    suspend fun getReportById(id: Int): StrayReport {
        return dao.getReportById(id)
    }

    suspend fun insert(report: StrayReport) {
        dao.insert(report)
    }

    suspend fun delete(report: StrayReport) {
        dao.delete(report)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
