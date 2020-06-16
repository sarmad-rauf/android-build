package com.es.marocapp.locale

import com.es.marocapp.model.responses.translations.TranslationInnerObject
import kotlin.collections.HashMap

object LocaleData{

    var stringsHashMap: Map<String?, TranslationInnerObject?>? = HashMap()

    fun getStringValue(key: String?): String? {
        var value = ""
        try {
            if (LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_EN)) {
                value = stringsHashMap?.get(key)?.english!!
            } else if (LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_FR)) {
                value = stringsHashMap?.get(key)?.french!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }
}