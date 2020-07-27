package com.es.marocapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.es.marocapp.R


class LanguageCustomSpinnerAdapter(
    val applicationContext: Context,
    var languages: Array<String>,var textColor : Int = applicationContext.resources.getColor(R.color.colorBlack)
) : BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(applicationContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.layout_custom_language_spinner, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        vh.label.text = languages[position]
        vh.label.setTextColor(textColor)
        return view
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return languages.size
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView = row?.findViewById(R.id.txtSpinnerItemText) as TextView

    }

}