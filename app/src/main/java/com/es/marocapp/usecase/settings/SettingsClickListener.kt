package com.es.marocapp.usecase.settings

import android.view.View

interface SettingsClickListener {

    fun onChangeLanguageClick(view: View)

    fun onBlockAccountClick(view: View)

    fun onUpdateClickListener(view:View)

    fun onBackButtonClickListener(view: View)
}