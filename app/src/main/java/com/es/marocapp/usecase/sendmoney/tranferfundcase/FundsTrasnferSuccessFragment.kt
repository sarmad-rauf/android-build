package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferSuccessBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel

class FundsTrasnferSuccessFragment : BaseFragment<FragmentFundsTransferSuccessBinding>(),
    FundsTrasnferClickLisntener {

    private lateinit var mActivityViewModel: SendMoneyViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_funds_transfer_success
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listner = this@FundsTrasnferSuccessFragment
            viewmodel = mActivityViewModel
        }

        (activity as SendMoneyActivity).setHeaderVisibility(false)

    }

    override fun onNextClickListner(view: View) {

    }

    override fun onBackClickListner(view: View) {
    }

}