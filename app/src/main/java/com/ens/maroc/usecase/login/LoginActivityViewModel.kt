package com.ens.maroc.usecase.login

import android.app.Application
import android.database.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val SPLASH_DISPLAY_LENGTH = 1000
    val mHandler = MutableLiveData<Boolean>()
    var isSignUpFlow : Boolean = false

    private fun postDelay() {

        android.os.Handler().postDelayed(Runnable {

            mHandler.postValue(true)

        }, SPLASH_DISPLAY_LENGTH.toLong())


    }


}