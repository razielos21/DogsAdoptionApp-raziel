package com.example.dogsadoptionapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dogsadoptionapp.data.model.Dog
import com.example.dogsadoptionapp.data.model.AdoptionRecord

@Database(entities = [Dog::class, AdoptionRecord::class], version = 1, exportSchema = false)
abstract class DogsDatabase : RoomDatabase() {
    abstract fun dogsDao(): DogsDao
    abstract fun adoptionRecordDao(): AdoptionRecordDao

    companion object {
        @Volatile
        private var INSTANCE: DogsDatabase? = null

        fun getDatabase(context: Context): DogsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DogsDatabase::class.java,
                    "dogs_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
