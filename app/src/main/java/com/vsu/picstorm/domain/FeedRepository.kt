package com.vsu.picstorm.domain

import android.graphics.Bitmap
import com.vsu.picstorm.domain.model.Publication
import com.vsu.picstorm.domain.model.enums.DateFilterType
import com.vsu.picstorm.domain.model.enums.ReactionType
import com.vsu.picstorm.domain.model.enums.SortFilterType
import com.vsu.picstorm.domain.model.enums.UserFilterType
import com.vsu.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun loadPhoto(token: String?, byteArray: ByteArray): Flow<ApiResult<Void>>

    suspend fun getFeed(
        token: String?,
        dateFilterType: DateFilterType,
        sortFilterType: SortFilterType,
        userFilterType: UserFilterType,
        specifiedId: Long?,
        index: Int,
        size: Int
    ): Flow<ApiResult<List<Publication>>>

    suspend fun getPhoto(publicationId: Long, width: Int): Flow<ApiResult<Bitmap>>
    suspend fun banPublication(token: String?, publicationId: Long): Flow<ApiResult<Void>>
    suspend fun deletePublication(token: String?, publicationId: Long): Flow<ApiResult<Void>>

    suspend fun reactPublication(
        token: String?,
        publicationId: Long,
        reactionType: ReactionType?
    ): Flow<ApiResult<ReactionType?>>
}