package com.es.marocapp.usecase.termsandcondiitons

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentContactUsBinding
import com.es.marocapp.usecase.BaseFragment

class ContactUsFragment : BaseFragment<FragmentContactUsBinding>(){

    private lateinit var mAcitivtyViewModel : TermsAndConditionsVIewModel

    override fun setLayout(): Int {
        return R.layout.fragment_contact_us
    }

    override fun init(savedInstanceState: Bundle?) {
        mAcitivtyViewModel = ViewModelProvider(this).get(TermsAndConditionsVIewModel::class.java)

        mDataBinding.apply {
            viewmodel = mAcitivtyViewModel
        }
    }

}