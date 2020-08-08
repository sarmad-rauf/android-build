package com.es.marocapp.usecase.favorites

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FavoritesTypeItemAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.databinding.FragmentFavoritesTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment

class FragmentFavoritesPaymentTypes : BaseFragment<FragmentFavoritesTypeBinding>(){

    lateinit var mActivityViewModel: FavoritesViewModel
    private lateinit var mFavoritesItemTypeAdapter: FavoritesTypeItemAdapter
    private var mFavoritesPaymentTypes: ArrayList<String>  = ArrayList()
    private var mFavoritesPaymentTypesIcon: ArrayList<Int>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_favorites_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as FavoritesActivity).get(FavoritesViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        (activity as FavoritesActivity).setHeader(mActivityViewModel.selectedFavoritesType.get()!!)

        mActivityViewModel.popBackStackTo = R.id.favoriteTypesFragment

        mFavoritesPaymentTypes.clear()
        mFavoritesPaymentTypes.apply {
            add(LanguageData.getStringValue("Bill").toString())
            add(LanguageData.getStringValue("WaterAndElectricity").toString())
        }

        mFavoritesPaymentTypesIcon.clear()
        mFavoritesPaymentTypesIcon.apply {
            add(R.drawable.bill_blue)
            add(R.drawable.water_electricty_blue)
        }

        mFavoritesItemTypeAdapter = FavoritesTypeItemAdapter(mFavoritesPaymentTypes,mFavoritesPaymentTypesIcon ,
            object : FavoritesTypeItemAdapter.FavoritesItemTypeClickListner{
                override fun onFavoriteItemTypeClick(itemType: String) {
                    if(itemType.equals(LanguageData.getStringValue("Bill"),true)){
                        mActivityViewModel.selectedFavoritesType.set(LanguageData.getStringValue("Bill"))
                        mActivityViewModel.isFatoratiUsecaseSelected.set(false)
                        (activity as FavoritesActivity).navController.navigate(R.id.action_fragmentFavoritesPaymentTypes_to_favoritesAddOrViewFragment)
                    }else if(itemType.equals(LanguageData.getStringValue("WaterAndElectricity"),true)){
                        mActivityViewModel.selectedFavoritesType.set(LanguageData.getStringValue("WaterAndElectricity"))
                        mActivityViewModel.isFatoratiUsecaseSelected.set(true)
                        (activity as FavoritesActivity).navController.navigate(R.id.action_fragmentFavoritesPaymentTypes_to_favoritesAddOrViewFragment)
                    }

                }

            })
        mDataBinding.mFavoritesTypeRecycler.apply {
            adapter = mFavoritesItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as FavoritesActivity)
        }

    }

}