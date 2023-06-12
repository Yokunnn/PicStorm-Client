package com.vsu.picstorm.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.ByteArrayOutputStream

class ImageCompressor {

    companion object {
        fun compress(image: Bitmap, size: Int): ByteArray {
            val width = image.width
            val height = image.height
            val matrix = Matrix()
            var scale = 1f
            var newImage: Bitmap

            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            var options = 100
            while (baos.toByteArray().size / 1024 > size && options != 0 && scale != 0.0f) {
                scale -= 0.05f
                matrix.postScale(scale, scale)
                newImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false)
                baos.reset()
                newImage.compress(Bitmap.CompressFormat.JPEG, options, baos)
                options -= 5
            }

            return baos.toByteArray()
        }
    }
}