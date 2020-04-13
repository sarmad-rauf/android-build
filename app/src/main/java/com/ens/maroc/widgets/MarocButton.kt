package com.ens.maroc.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.ens.maroc.utils.RootValues

class MarocButton : AppCompatButton {

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
        if (RootValues.getInstance().fontRubikMedium != null) {
            setTypeface(RootValues.getInstance().fontRubikMedium)
        }
    }

}