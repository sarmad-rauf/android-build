package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsAmountSelectionBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel

class FundsTransferAmountFragment : BaseFragment<FragmentFundsAmountSelectionBinding>(),
    FundsTrasnferClickLisntener {

    private lateinit var mActivityViewModel: SendMoneyViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_funds_amount_selection
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listner = this@FundsTransferAmountFragment
            viewmodel =mActivityViewModel
        }

        (activity as SendMoneyActivity).setHeaderTitle((activity as SendMoneyActivity).resources.getString(R.string.amount))
        (activity as SendMoneyActivity).setHeaderVisibility(true)

        mDataBinding.AmountSeekBar.max = mActivityViewModel.mBalanceInforAndResponseObserver.get()!!.balance!!.toInt()

    }

    override fun onNextClickListner(view: View) {

    }

    override fun onBackClickListner(view: View) {

    }

}