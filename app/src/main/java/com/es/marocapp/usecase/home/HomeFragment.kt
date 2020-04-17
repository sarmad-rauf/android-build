package com.es.marocapp.usecase.home

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentHomeBinding
import com.es.marocapp.usecase.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeViewModel: HomeViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_home
    }

    override fun init(savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        mDataBinding.apply {
            viewmodel = homeViewModel
        }

        homeViewModel.text.observe(this, Observer {
            text_home.text = it
        })
    }
}