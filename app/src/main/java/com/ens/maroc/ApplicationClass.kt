package com.ens.maroc

import android.app.Application
import com.ens.maroc.utils.RootValues


class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()

        RootValues.getInstance().initializeFonts(applicationContext)

    }


}