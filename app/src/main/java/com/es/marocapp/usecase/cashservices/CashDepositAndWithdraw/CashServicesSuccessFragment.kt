package com.es.marocapp.usecase.cashservices.CashDepositAndWithdraw

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashServicesSuccessBinding
import com.es.marocapp.locale.LanguageData
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

        mDataBinding.tvSendNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.tvSendNumberTitle.text = LanguageData.getStringValue("SenderNumber")
    }

    private fun updateUI() {

        mDataBinding.tvSenderNameVal.text = Constants.balanceInfoAndResponse.firstname + Constants.balanceInfoAndResponse.surname
        mDataBinding.tvSenderNumberVal.text = Constants.CURRENT_USER_MSISDN

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