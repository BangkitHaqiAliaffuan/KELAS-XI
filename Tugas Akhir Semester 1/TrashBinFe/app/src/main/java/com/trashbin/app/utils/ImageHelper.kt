package com.trashbin.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object ImageHelper {
    fun compressImage(context: Context, uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // Compress to 80% quality
            stream.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}