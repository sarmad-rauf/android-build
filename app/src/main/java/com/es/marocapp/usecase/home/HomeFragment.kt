package com.es.marocapp.usecase.home

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.HomeCardAdapter
import com.es.marocapp.adapter.HomeUseCasesAdapter
import com.es.marocapp.databinding.FragmentHomeBinding
import com.es.marocapp.model.CardModel
import com.es.marocapp.model.HomeUseCasesModel
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.github.islamkhsh.CardSliderIndicator
import com.github.islamkhsh.CardSliderViewPager
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mCardAdapter : HomeCardAdapter
    private lateinit var mUseCasesAdapter : HomeUseCasesAdapter

    override fun setLayout(): Int {
        return R.layout.fragment_home
    }

    override fun init(savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        mDataBinding.apply {
            viewmodel = homeViewModel
        }

        homeViewModel.text.observe(this, Observer {
        })

        populateHomeCardView()
        populateHomeUseCase()
    }

    private fun populateHomeUseCase() {
        val useCases = ArrayList<HomeUseCasesModel>().apply {
            this.add(HomeUseCasesModel(getString(R.string.recharge),R.drawable.ic_recharge))
            this.add(HomeUseCasesModel(getString(R.string.send_money),R.drawable.ic_send_money))
            this.add(HomeUseCasesModel(getString(R.string.transfer),R.drawable.ic_transfer))
            this.add(HomeUseCasesModel(getString(R.string.payments),R.drawable.ic_payment))
            this.add(HomeUseCasesModel(getString(R.string.qr),R.drawable.ic_qr_white))
            this.add(HomeUseCasesModel(getString(R.string.accounts),R.drawable.ic_accounts))
        }

        mUseCasesAdapter = HomeUseCasesAdapter(useCases)
        mDataBinding.useCasesRecyclerView.apply {
            adapter = mUseCasesAdapter
            layoutManager = GridLayoutManager(activity as MainActivity,3)
        }
    }

    private fun populateHomeCardView() {
        val cards = ArrayList<CardModel>().apply{
            // add items to arraylist
            this.add(CardModel("Credit Card","**** **** **** 1234","DH 1,200"))
            this.add(CardModel("Bank Card","**** **** **** 3333","DH 200"))
            this.add(CardModel("Loyalty Card","**** **** **** 2222","DH 2,000"))
            this.add(CardModel("Credit Card","**** **** **** 1234","DH 1,200"))
            this.add(CardModel("Bank Card","**** **** **** 3333","DH 200"))

        }
        mCardAdapter = HomeCardAdapter(cards)
        mDataBinding.viewPager.apply {
            adapter = mCardAdapter
        }

        mDataBinding.indicator.indicatorsToShow = cards.size
    }
}