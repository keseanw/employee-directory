package com.block.employeedirectory.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import com.jakewharton.disklrucache.DiskLruCache
import java.io.*


private const val DISK_CACHE_SIZE = 1024 * 1024 * 10 // 10MB
private const val IO_BUFFER_SIZE = 8 * 1024 // 8KB
private const val DISK_CACHE_SUBDIR = "employee_thumbnails"
private const val DISK_APP_VERSION = 1
private const val DISK_CACHE_ENTRY_AMT = 1

/**
 * Caches images to application and disk memory
 * Wrapper class for LruCache - memory cache
 * Wrapper class for DiskLruCache - disk cache
 *
 * Needs more refactoring to allow testing
* */


class ImageCache {

    private var diskLruCache: DiskLruCache? = null
    private val diskCacheLock = ReentrantLock()
    private val diskCacheLockCondition: Condition = diskCacheLock.newCondition()
    private var diskCacheStarting = true
    private lateinit var memoryCache: LruCache<String, Bitmap>

    suspend fun initializeCache(context: Context) {
        //memory cache
        initializeMemoryCache()

        withContext(Dispatchers.IO) {
            //disk cache
            try {
                initializeDiskCache(context)
            } catch (io: IOException) {
                //todo somewhat handles this occasional io exception when trying to write new journal for cache
                Log.e("initializeCache", "There was an issue initiating the disk cache -> ${io.message}")
            }
        }
    }

    private fun initializeMemoryCache() {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        memoryCache = object: LruCache<String, Bitmap>(cacheSize) {

            override fun sizeOf(key: String?, value: Bitmap?): Int {
                return (value?.byteCount ?: 0) / 1024
            }
        }
    }

    private fun initializeDiskCache(context: Context) {
        val cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR)

        diskCacheLock.withLock {
            //todo might be useful to allow DISK_CACHE_ENTRY_AMT to be configurable, maybe store small & large images per cache entry
            diskLruCache = DiskLruCache.open(
                cacheDir,
                DISK_APP_VERSION,
                DISK_CACHE_ENTRY_AMT,
                DISK_CACHE_SIZE.toLong()
            )

            diskCacheStarting = false // Finished initialization
            diskCacheLockCondition.signalAll() // Wake any waiting threads
        }
    }

    suspend fun addImageToCache(key: String, bitmap: Bitmap) {
        val formattedKey = key.formatUrlKey()

        //first add image to memory
        addImageToMemoryCache(key, bitmap)

        //lastly add image to disk
        withContext(Dispatchers.IO) {

            synchronized(diskCacheLock) {
                diskLruCache?.apply {
                    if (get(formattedKey) != null) {

                        //todo might be worth extracting this piece of logic into its own method
                        var bufferedOutputStream : BufferedOutputStream? = null
                        val lruEditor: DiskLruCache.Editor?
                        try {
                            lruEditor = edit(formattedKey)
                            bufferedOutputStream = BufferedOutputStream(lruEditor.newOutputStream(0), IO_BUFFER_SIZE)
                        } finally {
                            bufferedOutputStream?.close()
                        }

                        //todo might be worth extracting this piece of logic into its own method
                        val isBitmapOutComplete = bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bufferedOutputStream)
                        if(isBitmapOutComplete) {
                            flush()
                            lruEditor?.commit()
                            Log.i("addImageToCache", "Bitmap added to disk")
                        } else {
                            lruEditor?.abort()
                            Log.e("addImageToCache", "Bitmap not added to disk")
                        }
                    }
                }
            }
        }
    }

    private fun addImageToMemoryCache(key: String, bitmap: Bitmap) {
        if(this::memoryCache.isInitialized) {
            memoryCache.put(key, bitmap)
            Log.i("addImageToCache", "Bitmap added to memory ${memoryCache.get(key)}")
        }
    }

    //retrieves bitmap from memory
    fun getImageFromMemoryCache(key: String): Bitmap?  {
        if(this::memoryCache.isInitialized) {
            return memoryCache.get(key)
        }
        return null
    }

    //retrieves bitmap from disk
    fun getImageFromDiskCache(key: String): Bitmap? =
        diskCacheLock.withLock {
            val formattedKey = key.formatUrlKey()

            // Wait while disk cache is started from background thread
            while (diskCacheStarting) {
                try {
                    diskCacheLockCondition.await()
                } catch (e: InterruptedException) {
                }

            }
            return diskLruCache?.get(formattedKey).getBitmapFromDiskSnapshot()
        }

    suspend fun removeImageFromCaches(key: String) {
        removeImageFromMemoryCache(key)

        withContext(Dispatchers.IO) {
            removeImageFromDiskCache(key)
        }
    }

    private fun removeImageFromMemoryCache(key: String) {
        if(this::memoryCache.isInitialized) {
            memoryCache.remove(key)
        }
    }

    fun removeImageFromDiskCache(key: String) {
        val formattedKey = key.formatUrlKey()

        try {
            diskLruCache?.remove(formattedKey)
        } catch (e: IOException) {
            Log.e("removeImageFromCaches", "Issue removing image from disk cache:$e")
        }
    }

    suspend fun clearAllCaches() {
        clearMemoryCache()

        withContext(Dispatchers.IO) {
            clearDiskCache()
        }
    }

    private fun clearMemoryCache() {
        if(this::memoryCache.isInitialized) {
            memoryCache.evictAll()
        }
    }

    private fun clearDiskCache() {
        try {
            diskLruCache?.delete()
        } catch (e: IOException) {
            Log.e("clearDiskCache", "Issue clearing disk cache: $e")
        }
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    fun getDiskCacheDir(context: Context, uniqueName: String): File {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        val cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                || !Environment.isExternalStorageRemovable()) {
                context.externalCacheDir?.path
            } else {
                context.cacheDir.path
            }

        return File(cachePath + File.separator + uniqueName)
    }

    //converts DiskLruCache snapshot to a bitmap
    //takes the snapshot as an InputStream and decode the stream with BitmapFactory
    private fun DiskLruCache.Snapshot?.getBitmapFromDiskSnapshot(): Bitmap? {
        var bitmap: Bitmap? = null
        try{
            if(this == null) return null

            val inputStream = this.getInputStream(0)
            inputStream?.let {
                val buffInputStream = BufferedInputStream(it, IO_BUFFER_SIZE)
                bitmap = BitmapFactory.decodeStream( buffInputStream )
            }
        } catch (e: IOException) {
            Log.e("bitmapFromDiskSnapshot", "Issue getting bitmap from snapshot: ${e.message}")
        }
        return bitmap
    }

    //formats GUID (url string) keys as InputStreams for usage with the DiskLruCache
    private fun String.formatUrlKey() = this.byteInputStream().use { it.read().toString() }

    fun getMemoryCacheSize() = memoryCache.size()
    fun getDiskCacheSize() = diskLruCache?.size()

}