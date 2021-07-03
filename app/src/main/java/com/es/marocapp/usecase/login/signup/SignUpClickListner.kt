package com.es.marocapp.usecase.login.signup

import android.view.View

interface SignUpClickListner{
    fun onNextButtonClick(view: View)
    fun onBackButtonClick(view: View)
    fun onLevelOneButtonClick(view: View)
    fun onLevelTwoButtonClick(view: View)
    fun onCalenderCalenderClick(view: View)
    fun onGenderSelectionClick(view: View)
    fun onAtachFileClick(view: View)
}