package com.example.testproject.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.util.Log // Make sure to import Log

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

object TokenStore {

    suspend fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("access_token")] = accessToken
            prefs[stringPreferencesKey("refresh_token")] = refreshToken
        }
        Log.d("TokenStore", "Tokens saved: AccessToken=$accessToken, RefreshToken=$refreshToken")
    }

    suspend fun getAccessToken(context: Context): String? {
        // Move the logging BEFORE the return statement
        val accessToken = context.dataStore.data
            .map { prefs -> prefs[stringPreferencesKey("access_token")] }
            .first() // .first() is a terminal operator that collects the first value and then cancels the flow

        Log.d("TokenStore", "getAccessToken called. Retrieved token: $accessToken") // This will now execute
        return accessToken
    }

    suspend fun getRefreshToken(context: Context): String? {
        val refreshToken = context.dataStore.data
            .map { prefs -> prefs[stringPreferencesKey("refresh_token")] }
            .first()

        Log.d("TokenStore", "getRefreshToken called. Retrieved token: $refreshToken")
        return refreshToken
    }
}