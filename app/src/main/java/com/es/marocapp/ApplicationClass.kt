package com.es.marocapp

import android.app.Application
import com.es.marocapp.utils.RootValues


class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()

        RootValues.getInstance().initializeFonts(applicationContext)

    }


}