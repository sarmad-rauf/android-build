package com.es.marocapp.usecase.favorites

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentFavoritesTypeBinding
import com.es.marocapp.usecase.BaseFragment

class FavoriteTypesFragment : BaseFragment<FragmentFavoritesTypeBinding>(){

    lateinit var mActivityViewModel: FavoritesViewModel
    private lateinit var mFavoritesItemTypeAdapter: PaymentItemsAdapter
    private var mFavoritesTypes: ArrayList<String>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_favorites_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        mFavoritesTypes.apply {
            add("Payments")
            add("Transfer")
        }

        mFavoritesItemTypeAdapter = PaymentItemsAdapter(mFavoritesTypes, object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick() {
//                (activity as FavoritesActivity).navController.navigate(R.id.action_billTypeFragment_to_companyTypeFragment)
            }

        })
        mDataBinding.mFavoritesTypeRecycler.apply {
            adapter = mFavoritesItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as FavoritesActivity)
        }

    }

}