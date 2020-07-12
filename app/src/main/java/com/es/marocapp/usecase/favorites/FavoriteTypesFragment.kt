package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FavoritesTypeItemAdapter
import com.es.marocapp.databinding.FragmentFavoritesTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment

class FavoriteTypesFragment : BaseFragment<FragmentFavoritesTypeBinding>(){

    lateinit var mActivityViewModel: FavoritesViewModel
    private lateinit var mFavoritesItemTypeAdapter: FavoritesTypeItemAdapter
    private var mFavoritesTypes: ArrayList<String>  = ArrayList()
    private var mFavoritesTypesIcon: ArrayList<Int>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_favorites_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as FavoritesActivity).get(FavoritesViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        mActivityViewModel.popBackStackTo = -1

        (activity as FavoritesActivity).setHeader(LanguageData.getStringValue("Favorites").toString())


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFavoritesTypes.clear()
        mFavoritesTypes.apply {
            add(LanguageData.getStringValue("Payments").toString())
            add(LanguageData.getStringValue("SendMoney").toString())
            add(LanguageData.getStringValue("MerchantPayment").toString())
            add(LanguageData.getStringValue("AirTime").toString())
        }

        mFavoritesTypesIcon.clear()
        mFavoritesTypesIcon.apply {
            add(R.drawable.ic_favorite_payments)
            add(R.drawable.ic_favorite_transfers)
            add(R.drawable.ic_favorite_transfers)
            add(R.drawable.ic_favorite_transfers)
        }

        mFavoritesItemTypeAdapter = FavoritesTypeItemAdapter(mFavoritesTypes,mFavoritesTypesIcon ,
            object : FavoritesTypeItemAdapter.FavoritesItemTypeClickListner{
            override fun onFavoriteItemTypeClick(itemType: String) {
                if(itemType.equals(LanguageData.getStringValue("Payments"),true)){
                    mActivityViewModel.selectedFavoritesType.set(LanguageData.getStringValue("Payments"))
                    mActivityViewModel.isPaymentSelected.set(true)
                    (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteTypesFragment_to_fragmentFavoritesPaymentTypes)
                }else if(itemType.equals(LanguageData.getStringValue("SendMoney"),true)){
                    mActivityViewModel.selectedFavoritesType.set(LanguageData.getStringValue("SendMoney"))
                    mActivityViewModel.isPaymentSelected.set(false)
                    (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteTypesFragment_to_favoritesAddOrViewFragment)
                }else if(itemType.equals(LanguageData.getStringValue("MerchantPayment"),true)){
                    mActivityViewModel.selectedFavoritesType.set(LanguageData.getStringValue("MerchantPayment"))
                    mActivityViewModel.isPaymentSelected.set(false)
                    (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteTypesFragment_to_favoritesAddOrViewFragment)
                }else if(itemType.equals(LanguageData.getStringValue("AirTime"),true)){
                    mActivityViewModel.selectedFavoritesType.set(LanguageData.getStringValue("AirTime"))
                    mActivityViewModel.isPaymentSelected.set(false)
                    (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteTypesFragment_to_favoritesAddOrViewFragment)
                }
            }

        })
        mDataBinding.mFavoritesTypeRecycler.apply {
            adapter = mFavoritesItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as FavoritesActivity)
        }
    }

}