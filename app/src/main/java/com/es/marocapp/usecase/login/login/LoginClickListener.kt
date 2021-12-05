package com.es.marocapp.usecase.login.login

import android.view.View

interface LoginClickListener {

    fun onLoginButtonClick(view: View)

    fun onForgotPinClick(view: View)

    fun onAreYouNewClick(view: View)

    fun onSignUpClick(view: View)

    fun onTermsConditionsClick(view: View)
}