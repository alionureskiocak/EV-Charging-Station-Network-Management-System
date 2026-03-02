package com.example.fse_project.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.AppDatabase
import com.example.fse_project.data.repository.StationRepositoryImpl
import com.example.fse_project.domain.repository.StationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context : Context) : AppDatabase{
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "app_database"
        ).build()
    }

    @Singleton @Provides
    fun provideAppDao(db : AppDatabase) = db.appDao()

    @Singleton @Provides
    fun provideStationRepository(dao : AppDao) : StationRepository = StationRepositoryImpl(dao)
}