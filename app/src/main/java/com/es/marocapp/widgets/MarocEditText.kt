package com.es.marocapp.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.EditText
import com.es.marocapp.utils.RootValues

class MarocEditText : EditText {

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
        if (RootValues.getInstance().fontSofiaProRegular != null) {
            setTypeface(RootValues.getInstance().fontSofiaProRegular)
        }

        setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
    }

}