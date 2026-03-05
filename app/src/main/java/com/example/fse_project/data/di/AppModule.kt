package com.example.fse_project.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.work.impl.model.Preference
import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.AppDatabase
import com.example.fse_project.data.repository.ReservationRepositoryImpl
import com.example.fse_project.data.repository.StationRepositoryImpl
import com.example.fse_project.data.repository.UserRepositoryImpl
import com.example.fse_project.domain.repository.ReservationRepository
import com.example.fse_project.domain.repository.StationRepository
import com.example.fse_project.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.prefs.Preferences
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

    @Singleton @Provides
    fun provideUserRepository(dao : AppDao) : UserRepository = UserRepositoryImpl(dao)

    @Singleton @Provides
    fun provideReservationRepository(dao : AppDao) : ReservationRepository =
        ReservationRepositoryImpl(dao)

    @Singleton @Provides
    fun provideDataStore(@ApplicationContext context : Context) : DataStore<androidx.datastore.preferences.core.Preferences>{
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("users")
        }
    }
}