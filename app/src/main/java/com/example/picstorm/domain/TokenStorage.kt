package com.example.picstorm.domain

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.picstorm.di.dataStore
import com.example.picstorm.domain.model.Token
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenStorage @Inject constructor(
    private val context: Context
) {

    val token = getToken().asLiveData()

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }

    private fun getToken(): Flow<Token> {
        return context.dataStore.data.map { preferences ->
            Token(
                preferences[ACCESS_TOKEN_KEY].toString()
            )
        }
    }

    suspend fun saveToken(token: Token) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token.accessToken
        }
    }

    suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }
}