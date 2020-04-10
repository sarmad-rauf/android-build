package com.ens.maroc.locale

import android.content.Context
import com.ens.maroc.utils.Tools

object LocaleConfig {

    const val KEY_LANGUAGE_EN = "en"
    const val KEY_LANGUAGE_UR = "ur"

    /**
     * This function for getting selected Language Locale from shared preferences
     *
     * @param context
     */
    fun getSelectedLanguage(context: Context): String {

        var selectedLocale = KEY_LANGUAGE_EN

        try {
            if (context != null) {
                val selectedLanguageFromCache ="" /*PrefUtils.getString(context, PrefUtils.PreKeywords.PREF_KEY_SELECTED_LANGUAGE_LOCALE, KEY_LANGUAGE_EN)*/

                if (Tools.hasValue(selectedLanguageFromCache)) {
                    selectedLocale = selectedLanguageFromCache!!
                }
            }

        } catch (e: Exception) {
        }

        return selectedLocale

    }

/*
    *//**
     * This function  is used for saving selected Language Locale
     *
     * @param context
     *
     *//*
    fun setLanguageAndUpdate(activity: Activity?, selectedLanguageLocale: String?, clazz: Class<*>) {

        try {
            if (Tools.isActivityActive(activity) && Tools.hasValue(selectedLanguageLocale)) {
                if (Tools.hasValue(selectedLanguageLocale)) {
                    PrefUtils.addString(activity as Context, PrefUtils.PreKeywords.PREF_KEY_SELECTED_LANGUAGE_LOCALE, selectedLanguageLocale!!)
                }

                updateAppAfterlanguangeSelection(activity, clazz)
            }
        } catch (e: Exception) {
        }
    }

    *//**
     * This function is used to to udpate and restart activity after language selection
     *//*
    private fun updateAppAfterlanguangeSelection(activity: Activity?, clazz: Class<*>) {
        try {
            if (Tools.isActivityActive(activity)) {

                // clearing all cache after language switch
                NetworkCacheManager.clearCacheAfterLanguageChange(activity)
                // Activity Reloading
                (activity as BaseActivity<*>).startActivityAfterLanguageChange(activity, clazz)

            }
        } catch (e: Exception) {
        }
    }

    *//**
     * This function return True if Urdu language selected
     *//*
    fun isUrduSelected(context: Context): Boolean {
        var isUrduSelected = false

        try {
            if (context != null) {
                val selectedlanguage = getSelectedLanguage(context)

                if (KEY_LANGUAGE_UR.equals(selectedlanguage, true)) {
                    isUrduSelected = true
                }
            }
        } catch (e: Exception) {
        }

        return isUrduSelected
    }

    *//**
     * This function return True if English language selected
     *//*
    fun isEnglishSelected(context: Context): Boolean {
        var isEnglishSelected = false

        try {
            if (context != null) {
                val selectedlanguage = getSelectedLanguage(context)

                if (KEY_LANGUAGE_EN.equals(selectedlanguage, true)) {
                    isEnglishSelected = true
                }
            }
        } catch (e: Exception) {
        }

        return isEnglishSelected
    }

    *//**
     * This function return the code for selected language for Backend
     *//*
    fun getSelectedLanguageCodeForServer(context: Context): String {
        var languageCode = "1"
        if (isUrduSelected(context)) {
            languageCode = "2"
        }
        return languageCode
    }*/

}