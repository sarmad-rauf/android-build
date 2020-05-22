package com.es.marocapp.usecase.termsandcondiitons

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentTermsAndConditionBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.favorites.FavoritesViewModel

class TermsAndConditionFragment : BaseFragment<FragmentTermsAndConditionBinding>(){

    private lateinit var mAcitivtyViewModel : TermsAndConditionsVIewModel

    override fun setLayout(): Int {
        return R.layout.fragment_terms_and_condition
    }

    override fun init(savedInstanceState: Bundle?) {
        mAcitivtyViewModel = ViewModelProvider(this).get(TermsAndConditionsVIewModel::class.java)

        mDataBinding.apply {
            viewmodel = mAcitivtyViewModel
        }

    }

}