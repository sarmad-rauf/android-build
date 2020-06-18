package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentAirTimeConfirmationLayoutBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.airtime.AirTimeClickListner
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class AirTimeConfirmationFragment : BaseFragment<FragmentAirTimeConfirmationLayoutBinding>(),
    AirTimeClickListner {

    private lateinit var mActivityViewModel: AirTimeViewModel

    private var amountToTransfer = ""

    override fun setLayout(): Int {
        return R.layout.fragment_air_time_confirmation_layout
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as AirTimeActivity).get(AirTimeViewModel::class.java)
        mDataBinding.apply {
            listner = this@AirTimeConfirmationFragment
            viewmodel = mActivityViewModel
        }

        (activity as AirTimeActivity).setHeaderVisibility(false)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(false)

        mDataBinding.imgBackButton.setOnClickListener {
            (activity as AirTimeActivity).navController.popBackStack(
                R.id.airTimeMsisdnFragment,
                false
            )
        }

        setStrings()
        updateUI()
        subscribeObserver()
    }

    private fun setStrings() {
        //        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//
//        tvAmountVal == AmountTotal

        mDataBinding.tvCompanyNameTitle.text = LanguageData.getStringValue("ReceiverNumber")
        mDataBinding.tvOwnerNameTitle.text = LanguageData.getStringValue("ReceiverName")
        mDataBinding.tvReceiptCodeTitle.text = LanguageData.getStringValue("Bill")
        mDataBinding.tvDHTitle.text = LanguageData.getStringValue("Fee")
        mDataBinding.tvAmountTitle.text = LanguageData.getStringValue("TotalCost")

        mDataBinding.tvConfirmationTitle.text = LanguageData.getStringValue("Confirmation")

        mDataBinding.btnConfirmationCancel.text = LanguageData.getStringValue("BtnTitle_Cancel")
        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_Pay")
    }


    private fun subscribeObserver() {
        mActivityViewModel.getAirTimeResponseListner.observe(this@AirTimeConfirmationFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.HEADERS_FOR_PAYEMNTS = false
//                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
//                    mActivityViewModel.transactionID = it.financialTransactionId
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

    private fun updateUI() {
        //        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//
//        tvAmountVal == AmountTotal

        mDataBinding.tvCompanyNameVal.text = mActivityViewModel.transferdAmountTo
//        var ReceiverName = mActivityViewModel.mAccountHolderInfoResponseObserver.get()?.firstName +" " +mActivityViewModel.mAccountHolderInfoResponseObserver.get()?.sureName
//        mActivityViewModel.ReceiverName = ReceiverName
//        mDataBinding.tvOwnerNameVal.text = ReceiverName
        mDataBinding.tvReceiptCodeVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.amountToTransfer
        mDataBinding.tvDHVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.feeAmount

        amountToTransfer = Constants.addAmountAndFee(
            mActivityViewModel.amountToTransfer.toDouble(),
            mActivityViewModel.feeAmount.toDouble()
        )
        mDataBinding.tvAmountVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + amountToTransfer
    }

    override fun onNextClickListner(view: View) {
        DialogUtils.showPasswordDialoge(activity as AirTimeActivity,
            object : DialogUtils.OnPasswordDialogClickListner {
                override fun onDialogYesClickListner(password: String) {
                    Constants.HEADERS_FOR_PAYEMNTS = true
                    Constants.CURRENT_USER_CREDENTIAL = password

                    mActivityViewModel.requestForAirTimeApi(activity)
                }
            })
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isRechargeFixeUseCase.set(false)
        mActivityViewModel.isRechargeMobileUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as AirTimeActivity).startNewActivityAndClear(
            activity as AirTimeActivity,
            MainActivity::class.java
        )
    }

}