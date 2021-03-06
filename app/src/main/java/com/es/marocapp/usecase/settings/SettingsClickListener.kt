package com.es.marocapp.usecase.settings

import android.view.View

interface SettingsClickListener {

    fun onChangeLanguageClick(view: View)

    fun onManageFavoritesClick(view: View)

    fun onBlockAccountClick(view: View)

    fun onUpdateClickListener(view:View)

    fun onSetDefaultAccountClick(view:View)

    fun onBackButtonClickListener(view: View)
}