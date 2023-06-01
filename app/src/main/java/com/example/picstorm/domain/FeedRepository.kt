package com.example.picstorm.domain

import com.example.picstorm.util.Request
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FeedRepository {
    suspend fun loadPhoto(token: String?, byteArray: ByteArray): Flow<Request<Void?>>
}