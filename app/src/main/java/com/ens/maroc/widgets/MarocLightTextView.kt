package com.ens.maroc.widgets

import android.content.Context
import android.util.AttributeSet
import com.ens.maroc.utils.RootValues

class MarocLightTextView : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        if (RootValues.getInstance().fontRubikLight != null) {
            setTypeface(RootValues.getInstance().fontRubikLight)
        }
    }

}