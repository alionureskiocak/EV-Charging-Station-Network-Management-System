package com.example.fse_project.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

   private val USER_ID = longPreferencesKey("userId")

    val currentUserId : Flow<Long?> = dataStore.data
        .map {
            it[USER_ID] ?: 0
        }

    suspend fun setUserId(id : Long){
        dataStore.edit {
            it[USER_ID] = id
        }
    }
}