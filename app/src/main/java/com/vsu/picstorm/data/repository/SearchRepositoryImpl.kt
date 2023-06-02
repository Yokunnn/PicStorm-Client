package com.vsu.picstorm.data.repository

import com.vsu.picstorm.data.mapper.mapToDomain
import com.vsu.picstorm.data.service.SearchService
import com.vsu.picstorm.domain.SearchRepository
import com.vsu.picstorm.domain.model.UserLine
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.RequestUtils.requestFlow
import com.vsu.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: SearchService
) : SearchRepository {

    override suspend fun search(token: String?, name: String, index: Int, size: Int): Flow<ApiResult<List<UserLine>>> {
        return requestFlow (
            { searchService.search(createAuthHeader(token), name, index, size)},
            { value-> value?.mapToDomain()}
        )
    }
}