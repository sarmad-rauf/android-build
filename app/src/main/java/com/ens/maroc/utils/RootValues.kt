package com.ens.maroc.utils

import android.content.Context
import android.graphics.Typeface


class RootValues private constructor() {

    companion object {

        private var instance: RootValues? = null

        fun getInstance(): RootValues {
            if (instance == null) {
                instance = RootValues()
            }
            return instance as RootValues
        }

    }


    //--------------Generic Fonts Initialization--------------//

    var fontRubikBold: Typeface? = null
    var fontRubikLight: Typeface? = null
    var fontRubikMedium: Typeface? = null
    var fontRubikRegular: Typeface? = null

    fun initializeFonts(context: Context) {
        try {
            context?.assets?.let {
                fontRubikBold = Typeface.createFromAsset(context.assets, "font/Rubik-Bold.ttf")
                fontRubikLight = Typeface.createFromAsset(context.assets, "font/Rubik-Light.ttf")
                fontRubikMedium = Typeface.createFromAsset(context.assets, "font/Rubik-Medium.ttf")
                fontRubikRegular = Typeface.createFromAsset(context.assets, "font/Rubik-Regular.ttf")
            }
        } catch (e: Exception) {
        }
    }
}