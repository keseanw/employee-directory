package com.block.employeedirectory.ui.adapter

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

/**
 * Loads Images from network request
 *
 * Needs more refactoring to allow testing
**/
class ImageDownloader(imgCache: ImageCache, width: Int, height: Int) {

    private val mImgCache = imgCache
    private val mWidth = width
    private val mHeight = height
    private var inSampleSize = 0
    private var img: Bitmap? = null
    private var mImageResultListener: ImageResultListener? = null

    interface ImageResultListener {
        fun onError(err: Throwable)

        fun onSuccess(success: ImageBitmap)
    }

    fun setListener(imageResultListener: ImageResultListener) {
        mImageResultListener = imageResultListener
    }

    private suspend fun checkCachesForImage(url: String): Bitmap? {
        val diskCacheBitmap = withContext(Dispatchers.IO) {
            mImgCache.getImageFromDiskCache(url)
        }

        val memoryCachedImage = mImgCache.getImageFromMemoryCache(url)

        if(memoryCachedImage != null) return memoryCachedImage
        if(diskCacheBitmap != null) return diskCacheBitmap

        return null
    }

    //makes network call for image, in order to store image to cache
    private suspend fun downloadImage(imageUrl: String): Bitmap? {
        img = checkCachesForImage(imageUrl)
            if (img == null) {

                withContext(Dispatchers.IO) {

                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inSampleSize = inSampleSize
                    try {
                        val url = URL(imageUrl)
                        var connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                        var stream: InputStream = connection.inputStream
                        img = BitmapFactory.decodeStream(stream, null, options)

                        val imageWidth = options.outWidth
                        val imageHeight = options.outHeight

                        if (imageWidth > mWidth || imageHeight > mHeight) {
                            inSampleSize += 2
                            downloadImage(imageUrl)
                        } else {
                            options.inJustDecodeBounds = false
                            connection = url.openConnection() as HttpURLConnection
                            stream = connection.inputStream
                            img = BitmapFactory.decodeStream(stream, null, options)

                            return@withContext img
                        }
                    } catch (e: Exception) {
                        Log.e("downloadImage", "Issue downloading image: $e")
                        mImageResultListener?.onError(e)

                    }
            }
        }

        return img
    }

    suspend fun getImageResult(url: String): ImageBitmap? {
        val imageBitmapResult = downloadImage(url)

        if(imageBitmapResult != null) {
            mImgCache.addImageToCache(url, imageBitmapResult)
            mImageResultListener?.onSuccess(convertBitmapToImageBitmap(imageBitmapResult))
        }

        return imageBitmapResult?.let { convertBitmapToImageBitmap(it) }
    }

    private fun convertBitmapToImageBitmap(bitmap: Bitmap) = bitmap.asImageBitmap()

}