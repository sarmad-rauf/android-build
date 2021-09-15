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
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class AirTimeConfirmationFragment : BaseFragment<FragmentAirTimeConfirmationLayoutBinding>(),
    AirTimeClickListner {

    private lateinit var mActivityViewModel: AirTimeViewModel

    private var amountToTransfer = "0"

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
            if(mActivityViewModel.isQuickRechargeUseCase.get()!!){
                (activity as AirTimeActivity).finish()
            }else{
                (activity as AirTimeActivity).navController.popBackStack(
                    R.id.airTimeMainFragment,
                    false
                )
            }
        }

        setStrings()
        if(mActivityViewModel.isQuickRechargeUseCase.get()!!){
            mActivityViewModel.requestForAirTimeQuoteApi(activity,Constants.CURRENT_USER_MSISDN)
        }else{
            updateUI()
        }
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
        mDataBinding.tvReceiptCodeTitle.text = LanguageData.getStringValue("Recharge")
        mDataBinding.tvDHTitle.text = LanguageData.getStringValue("Fee")
        mDataBinding.tvAmountTitle.text = LanguageData.getStringValue("Amount")

        mDataBinding.tvConfirmationTitle.text = LanguageData.getStringValue("Confirmation")

        mDataBinding.btnConfirmationCancel.text = LanguageData.getStringValue("BtnTitle_Cancel")
        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_Pay")

        mDataBinding.tvSendNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.tvSendNumberTitle.text = LanguageData.getStringValue("SenderNumber")
    }


    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@AirTimeConfirmationFragment, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivityViewModel.getAirTimeQuoteResponseListner.observe(this@AirTimeConfirmationFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.totalTax=0.0
                        for(taxes in it.taxList.indices)
                        {
                            mActivityViewModel.totalTax=mActivityViewModel.totalTax+it.taxList[taxes].amount.amount.toString().toDouble()
                        }
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }

                    updateUI()
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })

        mActivityViewModel.getAirTimeResponseListner.observe(this@AirTimeConfirmationFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceafter
                    mActivityViewModel.transactionID = it.transactionId
                    DialogUtils.successFailureDialogue(activity as AirTimeActivity,it.description,0,object :DialogUtils.OnYesClickListner{
                        override fun onDialogYesClickListner() {
                            mActivityViewModel.isRechargeFixeUseCase.set(false)
                            mActivityViewModel.isRechargeMobileUseCase.set(false)
                            Constants.HEADERS_FOR_PAYEMNTS = false
                            (activity as AirTimeActivity).startNewActivityAndClear(activity as AirTimeActivity,
                                MainActivity::class.java)
                        }
                    })
               //     (activity as AirTimeActivity).navController.navigate(R.id.action_airTimeConfirmationFragment_to_airTimeSuccessFragment)
                } else if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity,it.description)
                } else {
                    if (it.responseCode.equals(ApiConstant.API_PENDING)) {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(true)
                        mActivityViewModel.isTransactionFailed.set(false)
                        (activity as AirTimeActivity).navController.navigate(R.id.action_airTimeConfirmationFragment_to_airTimePendingFragment)
                    } else {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(false)
                        mActivityViewModel.isTransactionFailed.set(true)
                        (activity as AirTimeActivity).navController.navigate(R.id.action_airTimeConfirmationFragment_to_airTimePendingFragment)
                    }
                }
            }
        )
    }

    private fun updateUI() {
        //        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//
//        tvAmountVal == AmountTotal

        mDataBinding.tvSenderNameVal.text =
            Constants.balanceInfoAndResponse?.firstname +" "+ Constants.balanceInfoAndResponse?.surname
        mDataBinding.tvSenderNumberVal.text = Constants.CURRENT_USER_MSISDN

        mDataBinding.tvCompanyNameVal.text = mActivityViewModel.transferdAmountTo
//        var ReceiverName = mActivityViewModel.mAccountHolderInfoResponseObserver.get()?.firstName +" " +mActivityViewModel.mAccountHolderInfoResponseObserver.get()?.sureName
//        mActivityViewModel.ReceiverName = ReceiverName
//        mDataBinding.tvOwnerNameVal.text = ReceiverName


        if(amountToTransfer.isNullOrEmpty())
        {
            amountToTransfer="0"
        }
        amountToTransfer = Constants.addAmountAndFee(
            mActivityViewModel.amountToTransfer.toDouble(),
            mActivityViewModel.feeAmount.toDouble()
        )

        if(mActivityViewModel.feeAmount.isNullOrEmpty())
        {
            mActivityViewModel.feeAmount="0"
        }
        val feeD=mActivityViewModel.feeAmount.toDouble()+mActivityViewModel.totalTax
        val fee= Constants.converValueToTwoDecimalPlace(feeD)

       val totalCostD=amountToTransfer.toDouble()+mActivityViewModel?.totalTax
       val totalCost = Constants.converValueToTwoDecimalPlace(totalCostD)

        mDataBinding.tvReceiptCodeVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.amountToTransfer
        mDataBinding.tvDHVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + fee
        mDataBinding.tvAmountVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + totalCost
        mDataBinding.receiverNameGroup.visibility = View.GONE

        if(Constants.IS_AGENT_USER)
        {
            mDataBinding.tvDHTitle.visibility=View.GONE
            mDataBinding.tvDHVal.visibility=View.GONE
        }
    }

    override fun onNextBtnClickListner(view: View) {
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