package com.vsu.picstorm.domain

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.asLiveData
import com.auth0.android.jwt.JWT
import com.vsu.picstorm.di.dataStore
import com.vsu.picstorm.domain.model.Token
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
            val accessToken = preferences[ACCESS_TOKEN_KEY]
            if (accessToken != null) {
                val jwta = JWT(accessToken)
                if (jwta.isExpired(0)) {
                    deleteToken()
                    Token(null)
                } else {
                    Token(accessToken)
                }
            } else{
                Token(null)
            }

        }
    }

    suspend fun saveToken(token: Token) {
        context.dataStore.edit { preferences ->
            if (token.accessToken != null) {
                preferences[ACCESS_TOKEN_KEY] = token.accessToken
            }
        }
    }

    suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }
}