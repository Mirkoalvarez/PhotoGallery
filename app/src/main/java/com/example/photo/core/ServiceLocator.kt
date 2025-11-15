package com.example.photo.core

import android.content.Context
import androidx.room.Room
import com.example.photo.BuildConfig
import com.example.photo.data.local.PhotoDatabase
import com.example.photo.data.remote.UnsplashApi
import com.example.photo.data.repository.DefaultPhotoRepository
import com.example.photo.data.repository.PhotoRepository

/**
 * Small service locator used to keep the sample free from DI frameworks.
 */
object ServiceLocator {

    @Volatile
    private var repository: PhotoRepository? = null

    @Volatile
    private var database: PhotoDatabase? = null

    fun provideRepository(context: Context): PhotoRepository {
        return repository ?: synchronized(this) {
            repository ?: createRepository(context.applicationContext).also { repository = it }
        }
    }

    private fun createRepository(appContext: Context): PhotoRepository {
        val db = database ?: Room.databaseBuilder(
            appContext,
            PhotoDatabase::class.java,
            "photos.db"
        )
            .fallbackToDestructiveMigration()
            .build()
            .also { database = it }

        val api = UnsplashApi.create(BuildConfig.UNSPLASH_ACCESS_KEY)
        val cacheTracker = CacheTracker(appContext)

        return DefaultPhotoRepository(
            unsplashApi = api,
            photoDao = db.photoDao(),
            cacheTracker = cacheTracker
        )
    }
}
