package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferConfirmationBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FundTransferConfirmationFragment : BaseFragment<FragmentFundsTransferConfirmationBinding>(),
    FundsTrasnferClickLisntener {

    private lateinit var mActivityViewModel: SendMoneyViewModel

    private var amountToTransfer = ""

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
        mActivityViewModel.popBackStackTo = R.id.fundsTransferAmountFragment
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
        mActivityViewModel.errorText.observe(this@FundTransferConfirmationFragment, Observer {
            Constants.HEADERS_FOR_PAYEMNTS = false
            DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it)
        })

        mActivityViewModel.getFloatTransferResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    mActivityViewModel.transactionID = it.financialTransactionId
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                }else{
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
                }
            })

        mActivityViewModel.getTransferResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    mActivityViewModel.transactionID = it.financialTransactionId
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                }else{
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
                }
            })

        mActivityViewModel.getMerchantPaymentResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    mActivityViewModel.transactionID = it.transactionId
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                }else{
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
                }
            })

        mActivityViewModel.getPaymentResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceafter
                    mActivityViewModel.transactionID = it.transactionId
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                }else{
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
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
        var ReceiverName = mActivityViewModel.mAccountHolderInfoResponseObserver.get()?.firstName +" " +mActivityViewModel.mAccountHolderInfoResponseObserver.get()?.sureName
        mActivityViewModel.ReceiverName = ReceiverName
        mDataBinding.tvOwnerNameVal.text = ReceiverName
        mDataBinding.tvReceiptCodeVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.amountToTransfer
        mDataBinding.tvDHVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.feeAmount

        amountToTransfer = Constants.addAmountAndFee(mActivityViewModel.amountToTransfer.toDouble(),mActivityViewModel.feeAmount.toDouble())
        mDataBinding.tvAmountVal.text =Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+amountToTransfer

    }

    override fun onNextClickListner(view: View) {
        DialogUtils.showPasswordDialoge(activity as SendMoneyActivity,object : DialogUtils.OnPasswordDialogClickListner{
            override fun onDialogYesClickListner(password: String) {
                Constants.HEADERS_FOR_PAYEMNTS = true
                Constants.CURRENT_USER_CREDENTIAL = password
                if(Constants.IS_AGENT_USER){
                    if(mActivityViewModel.isFundTransferUseCase.get()!!){
                        mActivityViewModel.requestForFloatTransferApi(activity,mActivityViewModel.qouteId)
                    }

                    if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                        if(mActivityViewModel.isUserRegistered.get()!!){
                            if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                                mActivityViewModel.requestFoMerchantApi(activity,Constants.CURRENT_USER_MSISDN,mActivityViewModel.qouteId)
                            }
                        }else{
                            if(mActivityViewModel.isAccountHolderInformationFailed.get()!!){
                                mActivityViewModel.requestForSimplePayementApi(activity,mActivityViewModel.qouteId,Constants.CURRENT_USER_MSISDN)
                            }else{
                                mActivityViewModel.requestFoPayementApi(activity,mActivityViewModel.qouteId,Constants.CURRENT_USER_MSISDN)
                            }
                        }
                    }

                }else{
                    if(mActivityViewModel.isUserRegistered.get()!!){
                        if(mActivityViewModel.isFundTransferUseCase.get()!!){
                            mActivityViewModel.requestFoTransferApi(activity,mActivityViewModel.qouteId)
                        }

                        if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                            mActivityViewModel.requestFoMerchantApi(activity,Constants.CURRENT_USER_MSISDN,mActivityViewModel.qouteId)
                        }
                    }else{
                        if(mActivityViewModel.isAccountHolderInformationFailed.get()!!){
                            mActivityViewModel.requestForSimplePayementApi(activity,mActivityViewModel.qouteId,Constants.CURRENT_USER_MSISDN)
                        }else{
                            mActivityViewModel.requestFoPayementApi(activity,mActivityViewModel.qouteId,Constants.CURRENT_USER_MSISDN)
                        }
                    }

                }
            }
        })
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isUserRegistered.set(false)
        mActivityViewModel.isFundTransferUseCase.set(false)
        mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as SendMoneyActivity).startNewActivityAndClear(activity as SendMoneyActivity,
            MainActivity::class.java)
    }

}