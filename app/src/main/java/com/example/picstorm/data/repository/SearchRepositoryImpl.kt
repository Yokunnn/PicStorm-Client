package com.example.picstorm.data.repository

import com.example.picstorm.data.mapper.mapToDomain
import com.example.picstorm.data.service.SearchService
import com.example.picstorm.domain.SearchRepository
import com.example.picstorm.domain.model.UserSearched
import com.example.picstorm.util.ApiResult
import com.example.picstorm.util.RequestUtils.requestFlow
import com.example.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: SearchService
) : SearchRepository {

    override suspend fun search(token: String?, name: String, index: Int, size: Int): Flow<ApiResult<List<UserSearched>>> {
        return requestFlow (
            { searchService.search(createAuthHeader(token), name, index, size)},
            { value-> value?.mapToDomain()}
        )
    }
}