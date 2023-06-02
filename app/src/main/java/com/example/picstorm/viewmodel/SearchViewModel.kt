package com.example.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.SearchRepositoryImpl
import com.example.picstorm.domain.model.UserSearched
import com.example.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepositoryImpl
) : ViewModel() {

    val searchResult = MutableLiveData<ApiResult<List<UserSearched>>>()

    fun search(token: String?, name: String, index: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.search(token, name, index, size).collect { result ->
                searchResult.postValue(result)
            }
        }
    }
}