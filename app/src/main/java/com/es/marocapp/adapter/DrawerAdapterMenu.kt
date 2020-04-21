package com.es.marocapp.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import nl.psdcompany.duonavigationdrawer.views.DuoOptionView


internal class DrawerAdapterMenu(
    options: ArrayList<String>,
    mNavigationViewIcons: ArrayList<Int>
) :
    BaseAdapter() {
    private var mOptions = ArrayList<String>()
    private var mOptionsIcons = ArrayList<Int>()
    private val mOptionViews =
        ArrayList<DuoOptionView>()

    override fun getCount(): Int {
        return mOptions.size
    }

    override fun getItem(position: Int): Any {
        return mOptions[position]
    }

    fun setViewSelected(position: Int, selected: Boolean) {

        // Looping through the options in the menu
        // Selecting the chosen option
        for (i in mOptionViews.indices) {
            if (i == position) {
                mOptionViews[i].isSelected = selected
            } else {
                mOptionViews[i].isSelected = !selected
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View,
        parent: ViewGroup
    ): View {
        val option = mOptions[position]
        val optionIcon = mOptionsIcons[position]

        // Using the DuoOptionView to easily recreate the demo
        val optionView: DuoOptionView
        optionView = if (convertView == null) {
            DuoOptionView(parent.context)
        } else {
            convertView as DuoOptionView
        }

        // Using the DuoOptionView's default selectors
        optionView.bind(option)

        // Adding the views to an array list to handle view selection
        mOptionViews.add(optionView)
        return optionView
    }

    init {
        mOptions = options
    }
}