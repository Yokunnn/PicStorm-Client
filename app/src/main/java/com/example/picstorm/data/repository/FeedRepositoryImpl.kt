package com.example.picstorm.data.repository

import com.example.picstorm.data.service.PublicationService
import com.example.picstorm.domain.FeedRepository
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestUtils.requestFlow
import com.example.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject


class FeedRepositoryImpl @Inject constructor(
    private val publicationService: PublicationService
): FeedRepository {

    override suspend fun loadPhoto(token: String?, byteArray: ByteArray): Flow<Request<Void?>> {
        return requestFlow{
            val filePart = MultipartBody.Part.createFormData(
                "uploadPicture",
                null,
                byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
            )
            publicationService.loadPhoto(createAuthHeader(token), filePart)
            null
        }
    }
}