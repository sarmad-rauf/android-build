package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentAirTimePendingBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.airtime.AirTimeClickListner
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.utils.Constants

class AirTimePendingFragment : BaseFragment<FragmentAirTimePendingBinding>(), AirTimeClickListner {

    private lateinit var mActivityViewModel: AirTimeViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_air_time_pending
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(AirTimeViewModel::class.java)
        mDataBinding.apply {
            listner = this@AirTimePendingFragment
            viewmodel = mActivityViewModel
        }

        (activity as AirTimeActivity).setHeaderVisibility(false)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(false)

        setStrings()
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

//        Constants.balanceInfoAndResponse.balance = mActivityViewModel.senderBalanceAfter

        mDataBinding.newBalanceVal.text = Constants.CURRENT_CURRENCY_TYPE+" "+Constants.balanceInfoAndResponse.balance
    }

    private fun setStrings() {
        mDataBinding.tvOwnerNameTitle.text = LanguageData.getStringValue("TransactionID")
        mDataBinding.tvContactNumTitle.text = LanguageData.getStringValue("ReceiverName")
        mDataBinding.tvReceiverNumberTitle.text = LanguageData.getStringValue("ReceiverNumber")
        mDataBinding.tvOwnerNameTitle2.text = LanguageData.getStringValue("Amount")
        mDataBinding.tvContactNumTitle2.text = LanguageData.getStringValue("Fee")
        mDataBinding.tvDHTitle.text = LanguageData.getStringValue("TotalCost")
        mDataBinding.newBalanceTitle.text = LanguageData.getStringValue("YourNewBalanceIs")

        mDataBinding.tvSuccessTitle.text = mActivityViewModel.airTimeSelected.get()!!
        mDataBinding.successTItle.text = LanguageData.getStringValue("Pending")

        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_OK")
        mDataBinding.tvCompanyNameTitle.text = LanguageData.getStringValue("Source")
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