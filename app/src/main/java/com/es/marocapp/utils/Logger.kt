package com.es.marocapp.utils


import android.util.Log
import com.es.marocapp.BuildConfig


object Logger {

    public val TAG_CATCH_LOGS = "Maroc_logs"

    /**
     * @param tag
     * @param message
     */
    public fun debugLog(tag: String, message: String) {
        if (BuildConfig.LOG_ENABLED) {
            Log.d(tag, message)
        }
    }

    /**
     * @param tag
     * @param message
     */
    fun errorLog(tag: String, message: String) {
        if (BuildConfig.LOG_ENABLED) {
            Log.e(tag, message)
        }
    }

    /**
     * @param tag
     * @param message
     */
    fun warnLog(tag: String, message: String) {
        if (BuildConfig.LOG_ENABLED) {
            Log.w(tag, message)
        }
    }
}
