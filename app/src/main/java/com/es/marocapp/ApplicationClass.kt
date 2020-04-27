package com.es.marocapp

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.es.marocapp.network.ApiClient
import com.es.marocapp.utils.RootValues


class ApplicationClass : Application() {

    // MultiDex Library
    override fun attachBaseContext(context: Context) {
        try {
            super.attachBaseContext(context)
            MultiDex.install(this)
        } catch (e: Exception) {
        }
    }

    override fun onCreate() {
        super.onCreate()

        RootValues.getInstance().initializeFonts(applicationContext)

        // Inialize Network Calling // Retrofit
        ApiClient.newApiClientInstance.setInit(this)
    }


}