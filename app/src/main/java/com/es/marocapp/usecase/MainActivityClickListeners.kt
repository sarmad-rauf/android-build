package com.es.marocapp.usecase

import android.view.View

interface MainActivityClickListeners {
    fun onSideMenuDrawerIconClick(view : View)
    fun onDrawerMenuNotificationsClick(view : View)
    fun onDrawerMenuSettingsClick(view : View)
    fun onDrawerMenuFavoritesClick(view : View)
    fun onDrawerMenuLogOutClick(view : View)
    fun onAccountDetailClick(view : View)
}