package com.example.picstorm.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.FeedRepositoryImpl
import com.example.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepositoryImpl
) : ViewModel() {

    val loadResult = MutableLiveData<ApiResult<Void>>()

    fun loadPhoto(token: String?, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val compressed = compressBitmap(image)
            feedRepository.loadPhoto(token, compressed).collect{result ->
                loadResult.postValue(result)
            }
        }
    }

    private fun compressBitmap(image: Bitmap): ByteArray {
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