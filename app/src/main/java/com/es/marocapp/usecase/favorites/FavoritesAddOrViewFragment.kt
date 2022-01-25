package com.es.marocapp.usecase.favorites

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FavoritesTypeItemAdapter
import com.es.marocapp.databinding.FragmentFavoritesTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.utils.DialogUtils

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
            add(R.drawable.add_blue)
            add(R.drawable.view_blue)
        }

        mFavoritesItemTypeAdapter = FavoritesTypeItemAdapter(mFavoritesTypes,mFavoritesTypesIcon ,
            object : FavoritesTypeItemAdapter.FavoritesItemTypeClickListner{
                override fun onFavoriteItemTypeClick(itemType: String) {
                    if(itemType.equals(LanguageData.getStringValue("Add"),true)){
                        mActivityViewModel.selectedFavoritesAction.set(LanguageData.getStringValue("Add"))
                        if(mActivityViewModel.isPaymentSelected.get()!!){
                            if(mActivityViewModel.isFatoratiUsecaseSelected.get()!!){
                                mActivityViewModel.requestForFatoratiStepOneApi(activity)
                            }else{
                                (activity as FavoritesActivity).navController.navigate(R.id.action_favoritesAddOrViewFragment_to_favoriteEnterContactFragment)
                            }
                        }else{
                            (activity as FavoritesActivity).navController.navigate(R.id.action_favoritesAddOrViewFragment_to_favoriteEnterContactFragment)
                            mActivityViewModel.isFatoratiUsecaseSelected.set(false)
                        }
                    }else if(itemType.equals(LanguageData.getStringValue("View"),true)){
                        (activity as FavoritesActivity).navController.navigate(R.id.action_favoritesAddOrViewFragment_to_viewFavoritesFragment)
                    }

                }

            })
        mDataBinding.mFavoritesTypeRecycler.apply {
            adapter = mFavoritesItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as FavoritesActivity)
        }

        subscribeObserver()
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FavoritesAddOrViewFragment, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })
        mActivityViewModel.getFatoratiStepOneResponseListner.observe(this@FavoritesAddOrViewFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    (activity as FavoritesActivity).navController.navigate(R.id.action_favoritesAddOrViewFragment_to_favoriteDetailFragment)
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            }
        )
    }

}