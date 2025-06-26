package com.example.dogsadoptionapp.di

import android.content.Context
import androidx.room.Room
import com.example.dogsadoptionapp.data.db.*
import com.example.dogsadoptionapp.data.repository.DonationRepository
import com.example.dogsadoptionapp.data.repository.StrayReportRepository
import com.example.dogsadoptionapp.data.repository.AdoptionRecordRepository
import com.example.dogsadoptionapp.data.repository.DogsRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.dogsadoptionapp.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DogsDatabase {
        return Room.databaseBuilder(
            context,
            DogsDatabase::class.java,
            "dogs_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson) : Retrofit {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideDogsDao(db: DogsDatabase): DogsDao = db.dogsDao()

    @Provides
    fun provideDogsRepository(dao: DogsDao): DogsRepository = DogsRepository(dao)

    @Provides
    fun provideStrayReportDao(db: DogsDatabase): StrayReportDao = db.strayReportDao()

    @Provides
    fun provideStrayReportRepository(dao: StrayReportDao): StrayReportRepository =
        StrayReportRepository(dao)

    @Provides
    fun provideAdoptionRecordDao(db: DogsDatabase): AdoptionRecordDao = db.adoptionRecordDao()

    @Provides
    fun provideAdoptionRecordRepository(dao: AdoptionRecordDao): AdoptionRecordRepository =
        AdoptionRecordRepository(dao)

    @Provides
    fun provideDonationDao(db: DogsDatabase): DonationDao = db.donationDao()

    @Provides
    fun provideDonationRepository(dao: DonationDao): DonationRepository = DonationRepository(dao)
}
