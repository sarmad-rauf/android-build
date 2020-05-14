package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FavoritesTypeItemAdapter
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentFavoritesTypeBinding
import com.es.marocapp.usecase.BaseFragment

class FavoriteTypesFragment : BaseFragment<FragmentFavoritesTypeBinding>(){

    lateinit var mActivityViewModel: FavoritesViewModel
    private lateinit var mFavoritesItemTypeAdapter: FavoritesTypeItemAdapter
    private var mFavoritesTypes: ArrayList<String>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_favorites_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        (activity as FavoritesActivity).mDataBinding.tvFavoritesTitle.text = "Favorites"


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFavoritesTypes.apply {
            add("Payments")
            add("Transfer")
        }

        mFavoritesItemTypeAdapter = FavoritesTypeItemAdapter(mFavoritesTypes, object : FavoritesTypeItemAdapter.FavoritesItemTypeClickListner{
            override fun onFavoriteItemTypeClick(itemType: String) {
                if(itemType.equals("Payments",true)){
                    (activity as FavoritesActivity).mDataBinding.tvFavoritesTitle.text = "Payments"
                }else{
                    (activity as FavoritesActivity).mDataBinding.tvFavoritesTitle.text = "Transfer"
                }
                (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteTypesFragment_to_favoriteDetailFragment)

            }

        })
        mDataBinding.mFavoritesTypeRecycler.apply {
            adapter = mFavoritesItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as FavoritesActivity)
        }
    }

}