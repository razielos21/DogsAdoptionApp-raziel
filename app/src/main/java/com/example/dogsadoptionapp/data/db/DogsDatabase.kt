package com.example.dogsadoptionapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dogsadoptionapp.data.db.AdoptionRecordDao
import com.example.dogsadoptionapp.data.db.DogsDao
import com.example.dogsadoptionapp.data.db.DonationDao
import com.example.dogsadoptionapp.data.db.StrayReportDao
import com.example.dogsadoptionapp.data.model.AdoptionRecord
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.data.model.Donation
import com.example.dogsadoptionapp.data.model.StrayReport

@Database(
    entities = [Dog::class, AdoptionRecord::class, StrayReport::class, Donation::class],
    version = 5,
    exportSchema = false
)
abstract class DogsDatabase : RoomDatabase() {
    abstract fun dogsDao(): DogsDao
    abstract fun adoptionRecordDao(): AdoptionRecordDao
    abstract fun strayReportDao(): StrayReportDao
    abstract fun donationDao(): DonationDao
}