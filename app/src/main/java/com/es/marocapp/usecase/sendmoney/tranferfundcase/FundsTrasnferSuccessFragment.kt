package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferSuccessBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants

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
        setStrings()
        updateUI()

    }

    private fun setStrings() {
        mDataBinding.tvOwnerNameTitle.text = LanguageData.getStringValue("TransactionID")
        mDataBinding.tvContactNumTitle.text = LanguageData.getStringValue("ReceiverName")
        mDataBinding.tvReceiverNumberTitle.text = LanguageData.getStringValue("ReceiverNumber")
        mDataBinding.tvOwnerNameTitle2.text = LanguageData.getStringValue("Amount")
        mDataBinding.tvContactNumTitle2.text = LanguageData.getStringValue("Fee")
        mDataBinding.tvDHTitle.text = LanguageData.getStringValue("TotalCost")
        mDataBinding.newBalanceTitle.text = LanguageData.getStringValue("YourNewBalanceIs")

        mDataBinding.tvSuccessTitle.text = mActivityViewModel.trasferTypeSelected.get()!!
        mDataBinding.successTItle.text = LanguageData.getStringValue("Success")

        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_OK")
        mDataBinding.tvCompanyNameTitle.text = LanguageData.getStringValue("Source")

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
        mDataBinding.tvContactNumVal.text = mActivityViewModel.ReceiverName
        mDataBinding.tvReceiverNumberVal.text = mActivityViewModel.transferdAmountTo

        mDataBinding.tvOwnerNameVal2.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.amountToTransfer
        mDataBinding.tvContactNumVal2.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.feeAmount

        var amountToTransfer = Constants.addAmountAndFee(mActivityViewModel.amountToTransfer.toDouble(),mActivityViewModel.feeAmount.toDouble())
        mDataBinding.tvDHVal.text =Constants.CURRENT_CURRENCY_TYPE+" "+amountToTransfer

        Constants.balanceInfoAndResponse.balance = mActivityViewModel.senderBalanceAfter

        mDataBinding.newBalanceVal.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.senderBalanceAfter
    }

    override fun onNextClickListner(view: View) {
        mActivityViewModel.isUserRegistered.set(false)
        mActivityViewModel.isFundTransferUseCase.set(false)
        mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as SendMoneyActivity).startNewActivityAndClear(activity as SendMoneyActivity,MainActivity::class.java)
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isUserRegistered.set(false)
        mActivityViewModel.isFundTransferUseCase.set(false)
        mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as SendMoneyActivity).startNewActivityAndClear(activity as SendMoneyActivity,MainActivity::class.java)
    }

}