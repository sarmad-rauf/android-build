package com.es.marocapp.locale

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.PrefUtils
import com.es.marocapp.utils.Tools
import java.util.*


object LocaleManager {

    const val KEY_LANGUAGE_EN = "en"
    const val KEY_LANGUAGE_FR = "fr"
    const val KEY_LANGUAGE_AR = "ar"

    var selectedLanguage= KEY_LANGUAGE_EN
    var languageToBeChangedAfterAPI="";

    /**
     * This function for getting selected Language Locale from shared preferences
     *
     * @param context
     */
    fun getSelectedLanguageFromPref(context: Context): String {

        var selectedLocale = KEY_LANGUAGE_FR

        try {
            if (context != null) {
                val selectedLanguageFromCache = PrefUtils.getString(context, PrefUtils.PreKeywords.PREF_KEY_SELECTED_LANGUAGE_LOCALE, KEY_LANGUAGE_FR   )
                Logger.debugLog("ABRAR","seting lang from cache ${selectedLanguageFromCache}")
                if (Tools.hasValue(selectedLanguageFromCache)) {
                    selectedLocale = selectedLanguageFromCache!!
                }else{
                    selectedLocale = "fr"
                }
            }


        } catch (e: Exception) {
        }

        return selectedLocale

    }
    /**
     * This function  is used for saving selected Language Locale
     *
     * @param context
     *
     */

    fun setLanguageToPref(context: Context, selectedLanguageLocale: String?) {
        Logger.debugLog("ABRAR","seting lang ${selectedLanguage}")
        try {
            if ( Tools.hasValue(selectedLanguageLocale)) {
                if (Tools.hasValue(selectedLanguageLocale)) {
                    PrefUtils.addString(context, PrefUtils.PreKeywords.PREF_KEY_SELECTED_LANGUAGE_LOCALE, selectedLanguageLocale!!)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun setLanguageAndUpdate(activity: Activity?, selectedLanguageLocale: String?, clazz: Class<*>) {
        selectedLanguage= selectedLanguageLocale!!
        try {
            if ( Tools.hasValue(selectedLanguageLocale)) {
                if (Tools.hasValue(selectedLanguageLocale)) {
                    PrefUtils.addString(activity as Context, PrefUtils.PreKeywords.PREF_KEY_SELECTED_LANGUAGE_LOCALE, selectedLanguageLocale!!)
                }

                updateAppAfterlanguangeSelection(activity, clazz)
            }
        } catch (e: Exception) {
        }
    }
    /**
     *
     * This function is used to to udpate and restart activity after language selection
     */
    private fun updateAppAfterlanguangeSelection(activity: Activity?, clazz: Class<*>) {
        try {

            val configuration: Configuration = activity?.getResources()!!.getConfiguration()
            configuration.setLayoutDirection(Locale(selectedLanguage))
            activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics())
            // Activity Reloading
            (activity as BaseActivity<*>).startActivityAfterLanguageChange(activity, clazz)


        } catch (e: Exception) {
        }
    }
    /**
     *
     * This function return True if French language selected
     */
    fun isFrenchSelected(context: Context): Boolean {
        var isFrenchSelected = false

        try {
            if (context != null) {
                val selectedlanguage = getSelectedLanguageFromPref(context)

                if (KEY_LANGUAGE_FR.equals(selectedlanguage, true)) {
                    isFrenchSelected = true
                }
            }
        } catch (e: Exception) {
        }

        return isFrenchSelected
    }
    /**
     *
     * This function return True if English language selected
     */
    fun isEnglishSelected(context: Context): Boolean {
        var isEnglishSelected = false

        try {
            if (context != null) {
                val selectedlanguage = getSelectedLanguageFromPref(context)

                if (KEY_LANGUAGE_EN.equals(selectedlanguage, true)) {
                    isEnglishSelected = true
                }
            }
        } catch (e: Exception) {
        }

        return isEnglishSelected
    }

    fun updateResources(
        context: Context,
        language: String
    ): Context? {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val config = Configuration(res.getConfiguration())
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.getDisplayMetrics())
        }
        return context
    }

    fun setAppLanguage(context: Context,localeCode: String) {
        val resources: Resources = context.resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(localeCode.toLowerCase()))
        } else {
            config.locale = Locale(localeCode.toLowerCase())
        }
        resources.updateConfiguration(config, dm)

        setLanguageToPref(context,localeCode)
        Logger.debugLog("Language",localeCode)
    }

}