package com.example.picstorm.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class ImageCompressor {

    companion object {
        fun compress (image: Bitmap, size: Int): ByteArray {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos) // 100baos

            var options = 100
            while (baos.toByteArray().size / 1024 > size) {
                baos.reset() // baosbaos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos) // options%baos
                options -= 10 // 10
            }

            return baos.toByteArray()
        }
    }
}