package com.es.marocapp.usecase.cashservices.CashDepositAndWithdraw

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashServicesSuccessBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.cashservices.CashServicesClickListner
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants

class CashServicesSuccessFragment : BaseFragment<FragmentCashServicesSuccessBinding>(),
    CashServicesClickListner {

    private lateinit var mActivityViewModel: CashServicesViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_cash_services_success
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as CashServicesActivity).get(CashServicesViewModel::class.java)
        mDataBinding.apply {
            listner = this@CashServicesSuccessFragment
            viewmodel = mActivityViewModel
        }

        mActivityViewModel.popBackStackTo = R.id.cashServicesConfirmationFragment

        (activity as CashServicesActivity).setHeaderVisibility(false)
        updateUI()
    }

    private fun updateUI() {

        mDataBinding.tvOwnerNameVal.text = mActivityViewModel.transactionID

        mDataBinding.tvReceiverNumberVal.text = mActivityViewModel.transferdAmountTo

        mDataBinding.tvOwnerNameVal2.text =
            Constants.CURRENT_CURRENCY_TYPE + " " + mActivityViewModel.amountToTransfer
        mDataBinding.tvContactNumVal2.text =
            Constants.CURRENT_CURRENCY_TYPE + " " + mActivityViewModel.feeAmount

        var amountToTransfer = Constants.addAmountAndFee(
            mActivityViewModel.amountToTransfer.toDouble(),
            mActivityViewModel.feeAmount.toDouble()
        )
        mDataBinding.tvDHVal.text = Constants.CURRENT_CURRENCY_TYPE + " " + amountToTransfer

        Constants.balanceInfoAndResponse.balance = mActivityViewModel.senderBalanceAfter
        mDataBinding.newBalanceVal.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.senderBalanceAfter

    }

    override fun onNextClickListner(view: View) {
        mActivityViewModel.isDepositUseCase.set(false)
        mActivityViewModel.isWithdrawUseCase.set(false)
        (activity as CashServicesActivity).startNewActivityAndClear(
            activity as CashServicesActivity,
            MainActivity::class.java
        )
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isDepositUseCase.set(false)
        mActivityViewModel.isWithdrawUseCase.set(false)
        (activity as CashServicesActivity).startNewActivityAndClear(
            activity as CashServicesActivity,
            MainActivity::class.java
        )
    }

}