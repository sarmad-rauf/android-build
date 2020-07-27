package com.es.marocapp.usecase.home


import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentHomeBalanceLayoutBinding
import com.es.marocapp.databinding.LayoutHomeScreenBalanceViewBinding
import com.es.marocapp.model.CardModel
import com.es.marocapp.usecase.BaseFragment

class HomeBalanceFragment(cardDataModel : CardModel) : BaseFragment<LayoutHomeScreenBalanceViewBinding>() {

    private lateinit var homeViewModel: HomeViewModel
    private  var mCardModel: CardModel = cardDataModel

    private fun updateBalance() {
//        mDataBinding.imgBalanceIcon.setImageResource(mCardModel.cardIcon)
        mDataBinding.progressValueTitle.text = mCardModel.cardName
        mDataBinding.progressValue.text = mCardModel.cardBalance
    }

    override fun setLayout(): Int {
        return R.layout.layout_home_screen_balance_view
    }

    override fun init(savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        mDataBinding.apply {
            viewmodel = homeViewModel
        }

        updateBalance()
    }

}
