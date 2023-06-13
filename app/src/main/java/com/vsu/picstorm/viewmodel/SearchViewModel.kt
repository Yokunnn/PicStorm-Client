package com.vsu.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.picstorm.data.repository.SearchRepositoryImpl
import com.vsu.picstorm.domain.model.UserLine
import com.vsu.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepositoryImpl
) : ViewModel() {

    lateinit var searchResult: MutableLiveData<Pair<Int, ApiResult<List<UserLine>>>>

    fun init() {
        searchResult = MutableLiveData<Pair<Int, ApiResult<List<UserLine>>>>()
    }

    fun search(token: String?, name: String, index: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.search(token, name, index, size).collect { result ->
                searchResult.postValue(Pair(index, result))
            }
        }
    }
}