package com.es.marocapp.usecase

import android.view.View

interface MainActivityClickListeners {
    fun onSideMenuDrawerIconClick(view : View)
    fun onDrawerMenuNotificationsClick(view : View)
    fun onDrawerMenuSettingsClick(view : View)
    fun onDrawerMenuFavoritesClick(view : View)
    fun onDrawerMenuContactUsClick(view : View)
    fun onDrawerMenuFAQsClick(view : View)
    fun onDrawerMenuTermsAndConditionClick(view : View)
    fun onDrawerMenuLogOutClick(view : View)
    fun onDrawerMenuGenerateQRClick(view : View)
    fun onAccountDetailClick(view : View)

}