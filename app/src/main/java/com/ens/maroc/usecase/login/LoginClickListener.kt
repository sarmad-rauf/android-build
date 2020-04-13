package com.ens.maroc.usecase.login

import android.view.View

interface LoginClickListener {

    fun onLoginButtonClick(view: View)

    fun onForgotPinClick(view: View)

    fun onSignUpClick(view: View)
}