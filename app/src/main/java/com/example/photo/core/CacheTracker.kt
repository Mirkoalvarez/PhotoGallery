package com.example.photo.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists cache metadata that is shown in the settings screen.
 */
class CacheTracker(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun markUpdated(timestamp: Long = System.currentTimeMillis()) {
        sharedPreferences.edit()
            .putLong(KEY_LAST_UPDATE, timestamp)
            .apply()
    }

    fun clear() {
        sharedPreferences.edit()
            .remove(KEY_LAST_UPDATE)
            .apply()
    }

    fun lastUpdateTimestamp(): Long = sharedPreferences.getLong(KEY_LAST_UPDATE, 0L)

    private companion object {
        const val PREFS_NAME = "photo_cache_prefs"
        const val KEY_LAST_UPDATE = "last_update"
    }
}
