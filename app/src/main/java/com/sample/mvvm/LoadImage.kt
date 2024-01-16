package com.sample.mvvm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class LoadImage(val callback: LoadImageCallback) : AsyncTask<String?, Void?, Bitmap?>() {
    companion object {
        private val TAG = this::class.java.simpleName
        private const val DEBUG = false
    }

    private var id: String? = null
    private var time: String? = null

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: String?): Bitmap? {
        id = params[0]
        time = params[1]
        try {
            val url = URL(params[2])
            val bitmap = BitmapFactory.decodeStream(url.openStream())
            val file: File = callback.getFileStreamPath()
            if (!file.exists()) {
                file.mkdirs()
            }
            val outFile = File(file.path, "$id.png")
            val out = FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, out)
            out.flush()
            out.close()
            callback.putBitmap(id, time, bitmap)
            return bitmap
        } catch (e: Exception) {
            return null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(bitmap: Bitmap?) {
        super.onPostExecute(bitmap)
        callback.setBitmap(id, bitmap)
    }

    interface LoadImageCallback {
        fun getLruCache(): LruCache<String?, Bitmap>
        fun getKey(): String
        fun getUpdateTime(): String
        fun getUrl(): String?
        fun getDefaultIcon(): Int
        fun getPathName(): String
        fun getFileStreamPath(): File
        fun putBitmap(id: String?, time: String?, bitmap: Bitmap?)
        fun setBitmap(id: String?, bitmap: Bitmap?)
    }
}