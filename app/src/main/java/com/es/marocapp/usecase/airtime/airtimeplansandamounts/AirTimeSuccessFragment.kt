package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentAirTimeSuccessLayoutBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.airtime.AirTimeClickListner
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.utils.Constants

class AirTimeSuccessFragment : BaseFragment<FragmentAirTimeSuccessLayoutBinding>(),
    AirTimeClickListner {

    private lateinit var mActivityViewModel: AirTimeViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_air_time_success_layout
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(AirTimeViewModel::class.java)
        mDataBinding.apply {
            listner = this@AirTimeSuccessFragment
            viewmodel = mActivityViewModel
        }

        (activity as AirTimeActivity).setHeaderVisibility(false)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(false)

        updateUI()
    }

    private fun updateUI() {
//        tvOwnerNameVal== TransactionID
//        tvContactNumVal== ReceiverName
//        tvReceiverNumberVal= ReceiverNumber
//        tvOwnerNameVal2 == Amount
//        tvContactNumVal2== Fee
//
//        tvDHVal== AmountTotal
//
//        newBalanceVal = NewBalance

        mDataBinding.tvOwnerNameVal.text = mActivityViewModel.transactionID
//        mDataBinding.tvContactNumVal.text = mActivityViewModel.ReceiverName
        mDataBinding.tvReceiverNumberVal.text = mActivityViewModel.transferdAmountTo

        mDataBinding.tvOwnerNameVal2.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.amountToTransfer
        mDataBinding.tvContactNumVal2.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.feeAmount

        var amountToTransfer = Constants.addAmountAndFee(mActivityViewModel.amountToTransfer.toDouble(),mActivityViewModel.feeAmount.toDouble())
        mDataBinding.tvDHVal.text = Constants.CURRENT_CURRENCY_TYPE+" "+amountToTransfer

        Constants.balanceInfoAndResponse.balance = mActivityViewModel.senderBalanceAfter

        mDataBinding.newBalanceVal.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.senderBalanceAfter
    }

    override fun onNextClickListner(view: View) {
        mActivityViewModel.isRechargeFixeUseCase.set(false)
        mActivityViewModel.isRechargeMobileUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as AirTimeActivity).startNewActivityAndClear(activity as AirTimeActivity,
            MainActivity::class.java)
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isRechargeFixeUseCase.set(false)
        mActivityViewModel.isRechargeMobileUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as AirTimeActivity).startNewActivityAndClear(activity as AirTimeActivity,
            MainActivity::class.java)
    }

}