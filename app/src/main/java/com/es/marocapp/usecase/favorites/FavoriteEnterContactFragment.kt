package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFavoritesEnterNumberBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity

class FavoriteEnterContactFragment : BaseFragment<FragmentFavoritesEnterNumberBinding>(),
    FavoritesPaymentClickListener {

    private lateinit var mActivitViewModel: FavoritesViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_favorites_enter_number
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivitViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivitViewModel
            listener = this@FavoriteEnterContactFragment
        }

    }

    override fun onNextButtonClick(view: View) {
        (activity as FavoritesActivity).startNewActivityAndClear(activity as FavoritesActivity,MainActivity::class.java)
    }

}