package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferConfirmationBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel

class FundTransferConfirmationFragment : BaseFragment<FragmentFundsTransferConfirmationBinding>(),
    FundsTrasnferClickLisntener {

    private lateinit var mActivityViewModel: SendMoneyViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_funds_transfer_confirmation
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listner = this@FundTransferConfirmationFragment
            viewmodel = mActivityViewModel
        }

        (activity as SendMoneyActivity).setHeaderVisibility(false)
    }

    override fun onNextClickListner(view: View) {
    }

    override fun onBackClickListner(view: View) {
    }

}