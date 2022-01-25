package com.es.marocapp.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.CardModel
import com.es.marocapp.model.responses.BalanceInfoAndLimitResponse
import com.es.marocapp.usecase.home.HomeBalanceFragment
import com.es.marocapp.utils.Constants

class HomeCardAdapter(
    fm: FragmentManager?,
    var listOfFragment: ArrayList<HomeBalanceFragment>
) :
    FragmentStatePagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        return listOfFragment[position]
   }

    override fun getCount(): Int {
        return listOfFragment.size
    }


}