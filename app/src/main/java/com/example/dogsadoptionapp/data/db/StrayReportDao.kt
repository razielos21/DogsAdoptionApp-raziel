package com.example.dogsadoptionapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dogsadoptionapp.data.model.StrayReport

@Dao
interface StrayReportDao {

    @Query("SELECT * FROM stray_reports ORDER BY timestamp DESC")
    fun getAllReports(): LiveData<List<StrayReport>>

    @Query("SELECT * FROM stray_reports WHERE id = :id")
    suspend fun getReportById(id: Int): StrayReport

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: StrayReport)

    @Delete
    suspend fun delete(report: StrayReport)
}
