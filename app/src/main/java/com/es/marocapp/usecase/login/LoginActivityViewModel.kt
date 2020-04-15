package com.es.marocapp.usecase.login

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val SPLASH_DISPLAY_LENGTH = 1000
    val mHandler = MutableLiveData<Boolean>()
    var isSignUpFlow : ObservableField<Boolean> = ObservableField(false)

    private fun postDelay() {

        android.os.Handler().postDelayed(Runnable {

            mHandler.postValue(true)

        }, SPLASH_DISPLAY_LENGTH.toLong())


    }


}