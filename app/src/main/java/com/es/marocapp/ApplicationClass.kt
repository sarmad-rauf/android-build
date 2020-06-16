package com.es.marocapp

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDex
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.network.ApiClient
import com.es.marocapp.utils.RootValues
import java.util.*


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

        // Set App saved Language
        LocaleManager.selectedLanguage=LocaleManager.getSelectedLanguageFromPref(applicationContext)
        LocaleManager.setAppLanguage(applicationContext,LocaleManager.selectedLanguage)
    }



}