package com.es.marocapp

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDex
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.network.ApiClient
import com.es.marocapp.utils.RootValues
import com.google.android.gms.security.ProviderInstaller
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.SSLContext


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


        val configuration: Configuration = getResources()!!.getConfiguration()
        configuration.setLayoutDirection(Locale(LocaleManager.selectedLanguage))
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics())

        //SSL pinning
        installSSL()

    }


    fun installSSL(){

            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            var sslContext: SSLContext
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();

    }



}