package com.example.picstorm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.SearchRepositoryImpl
import com.example.picstorm.data.repository.SubscribeRepositoryImpl
import com.example.picstorm.domain.model.UserSearched
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepositoryImpl
) : ViewModel() {

    val searchReqState = MutableLiveData<RequestState>()
    val items = MutableLiveData<MutableList<UserSearched>>()
    var temp: MutableList<UserSearched> = emptyList<UserSearched>().toMutableList()

    fun search(token: String?, name: String, index: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.search(token, name, index, size).collect { requestState ->
                when (requestState) {
                    is Request.Error -> {
                        Log.e("Error", requestState.message)
                        searchReqState.postValue(RequestState.ERROR)
                    }
                    is Request.Loading -> {
                        searchReqState.postValue(RequestState.LOADING)
                    }
                    is Request.Success -> {
                        if (index == 0) {
                            temp = emptyList<UserSearched>().toMutableList()
                        }
                        temp.addAll(requestState.data)
                        items.postValue(temp)
                        searchReqState.postValue(RequestState.SUCCESS)
                    }
                }
            }
        }
    }
}