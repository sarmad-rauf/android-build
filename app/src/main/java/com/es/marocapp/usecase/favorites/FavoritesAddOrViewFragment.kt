package com.es.marocapp.usecase.favorites

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FavoritesTypeItemAdapter
import com.es.marocapp.databinding.FragmentFavoritesTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment

class FavoritesAddOrViewFragment : BaseFragment<FragmentFavoritesTypeBinding>(){

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

        (activity as FavoritesActivity).setHeader(mActivityViewModel.selectedFavoritesType.get()!!)

        if(mActivityViewModel.isPaymentSelected.get()!!){
            mActivityViewModel.popBackStackTo = R.id.fragmentFavoritesPaymentTypes
        }else{
            mActivityViewModel.popBackStackTo = R.id.favoriteTypesFragment
        }

        mFavoritesTypes.clear()
        mFavoritesTypes.apply {
            add(LanguageData.getStringValue("Add").toString())
            add(LanguageData.getStringValue("View").toString())
        }

        mFavoritesTypesIcon.clear()
        mFavoritesTypesIcon.apply {
            add(R.drawable.add)
            add(R.drawable.view)
        }

        mFavoritesItemTypeAdapter = FavoritesTypeItemAdapter(mFavoritesTypes,mFavoritesTypesIcon ,
            object : FavoritesTypeItemAdapter.FavoritesItemTypeClickListner{
                override fun onFavoriteItemTypeClick(itemType: String) {
                    if(itemType.equals(LanguageData.getStringValue("Add"),true)){
                        mActivityViewModel.selectedFavoritesAction.set(LanguageData.getStringValue("Add"))
                        if(mActivityViewModel.isPaymentSelected.get()!!){
                            if(mActivityViewModel.isFatoratiUsecaseSelected.get()!!){
                                (activity as FavoritesActivity).navController.navigate(R.id.action_favoritesAddOrViewFragment_to_favoriteDetailFragment)
                            }else{
                                (activity as FavoritesActivity).navController.navigate(R.id.action_favoritesAddOrViewFragment_to_favoriteEnterContactFragment)
                            }
                        }else{
                            (activity as FavoritesActivity).navController.navigate(R.id.action_favoritesAddOrViewFragment_to_favoriteEnterContactFragment)
                        }
                    }else if(itemType.equals(LanguageData.getStringValue("View"),true)){
                        mActivityViewModel.selectedFavoritesAction.set(LanguageData.getStringValue("SendMoney"))
                    }

                }

            })
        mDataBinding.mFavoritesTypeRecycler.apply {
            adapter = mFavoritesItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as FavoritesActivity)
        }
    }

}