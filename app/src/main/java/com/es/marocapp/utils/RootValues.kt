package com.es.marocapp.utils

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

  //  var fontRubikBold: Typeface? = null
    var fontRubikLight: Typeface? = null
    var fontRubikMedium: Typeface? = null
 //   var fontRubikRegular: Typeface? = null

    var fontSofiaProBold: Typeface? = null
    var fontSofiaProRegular: Typeface? = null

    fun initializeFonts(context: Context) {
        try {
            context?.assets?.let {
            //    fontRubikBold = Typeface.createFromAsset(context.assets, "fonts/Rubik-Bold.ttf")
                fontRubikLight = Typeface.createFromAsset(context.assets, "fonts/Rubik-Light.ttf")
                fontRubikMedium = Typeface.createFromAsset(context.assets, "fonts/Rubik-Medium.ttf")
             //   fontRubikRegular = Typeface.createFromAsset(context.assets, "fonts/Rubik-Regular.ttf")
                fontSofiaProBold = Typeface.createFromAsset(context.assets, "fonts/SofiaPro-Bold.ttf")
                fontSofiaProRegular = Typeface.createFromAsset(context.assets, "fonts/SofiaProRegular.ttf")
            }
        } catch (e: Exception) {
        }
    }
}