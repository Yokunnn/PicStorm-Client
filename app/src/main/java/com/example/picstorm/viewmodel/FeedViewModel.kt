package com.example.picstorm.viewmodel

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.FeedRepositoryImpl
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepositoryImpl
) : ViewModel() {

    val loadReqState = MutableLiveData<RequestState>()

    fun loadPhoto(token: String?, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val compressed = compressBitmap(image)
            feedRepository.loadPhoto(token, compressed).collect { requestState ->
                when (requestState) {
                    is Request.Error -> {
                        loadReqState.postValue(RequestState.ERROR)
                    }
                    is Request.Loading -> {
                        loadReqState.postValue(RequestState.LOADING)
                    }
                    is Request.Success -> {
                        loadReqState.postValue(RequestState.SUCCESS)
                    }
                }
            }
        }
    }

    fun compressBitmap(image: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos) // 100baos

        var options = 100
        while (baos.toByteArray().size / 1024 > 1024) { // 1Mb,
            baos.reset() // baosbaos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos) // options%baos
            options -= 10 // 10
        }

        return baos.toByteArray()
    }
}