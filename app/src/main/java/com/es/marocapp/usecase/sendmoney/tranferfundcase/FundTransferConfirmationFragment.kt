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
import kotlin.math.round

class FundTransferConfirmationFragment : BaseFragment<FragmentFundsTransferConfirmationBinding>(),
    FundsTrasnferClickLisntener {

    private lateinit var mActivityViewModel: SendMoneyViewModel

    private var amountToTransfer = "0"

    override fun setLayout(): Int {
        return R.layout.fragment_funds_transfer_confirmation
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listner = this@FundTransferConfirmationFragment
            viewmodel = mActivityViewModel
        }

        (activity as SendMoneyActivity).setHeaderVisibility(false)

        mActivityViewModel.popBackStackTo = R.id.fundsTransferAmountFragment

        mDataBinding.imgBackButton.setOnClickListener {
            (activity as SendMoneyActivity).navController.popBackStack(
                R.id.fundsTransferAmountFragment,
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
        if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
            mDataBinding.tvReceiptCodeTitle.text = LanguageData.getStringValue("Amount")
        }else{
            mDataBinding.tvReceiptCodeTitle.text = LanguageData.getStringValue("Bill")
        }
        mDataBinding.tvDHTitle.text = LanguageData.getStringValue("Fee")
        mDataBinding.tvAmountTitle.text = LanguageData.getStringValue("TotalCost")

        mDataBinding.tvConfirmationTitle.text = LanguageData.getStringValue("Confirmation")

        mDataBinding.btnConfirmationCancel.text = LanguageData.getStringValue("BtnTitle_Cancel")
        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_Validate")

        mDataBinding.tvSendNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.tvSendNumberTitle.text = LanguageData.getStringValue("SenderNumber")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FundTransferConfirmationFragment, Observer {
            Constants.HEADERS_FOR_PAYEMNTS = false
            DialogUtils.showErrorDialoge(activity as SendMoneyActivity, it)
        })

        mActivityViewModel.getFloatTransferResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    mActivityViewModel.transactionID = it.financialTransactionId
                    // (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                    DialogUtils.successFailureDialogue(activity as SendMoneyActivity,it.description,0,object :DialogUtils.OnYesClickListner{
                        override fun onDialogYesClickListner() {
                            mActivityViewModel.isUserRegistered.set(false)
                            mActivityViewModel.isFundTransferUseCase.set(false)
                            mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
                            Constants.HEADERS_FOR_PAYEMNTS = false
                            (activity as SendMoneyActivity).startNewActivityAndClear(activity as SendMoneyActivity,MainActivity::class.java)
                        }
                    })
                }else if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity,it.description)
                } else {
                    if (it.responseCode.equals(ApiConstant.API_PENDING)) {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(true)
                        mActivityViewModel.isTransactionFailed.set(false)
                        mActivityViewModel.sendMoneyFailureOrPendingDescription.set(it.description)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTransferPendingFragment)
                    } else {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(false)
                        mActivityViewModel.isTransactionFailed.set(true)
                        mActivityViewModel.sendMoneyFailureOrPendingDescription.set(it.description)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTransferPendingFragment)
                    }
                }
            })

        mActivityViewModel.getTransferResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    mActivityViewModel.transactionID = it.financialTransactionId
                    // (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                    DialogUtils.successFailureDialogue(activity as SendMoneyActivity,it.description,0,object :DialogUtils.OnYesClickListner{
                        override fun onDialogYesClickListner() {
                            mActivityViewModel.isUserRegistered.set(false)
                            mActivityViewModel.isFundTransferUseCase.set(false)
                            mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
                            Constants.HEADERS_FOR_PAYEMNTS = false
                            (activity as SendMoneyActivity).startNewActivityAndClear(activity as SendMoneyActivity,MainActivity::class.java)
                        }
                    })
                }else if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity,it.description)
                } else {
                    if (it.responseCode.equals(ApiConstant.API_PENDING)) {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(true)
                        mActivityViewModel.isTransactionFailed.set(false)
                        mActivityViewModel.sendMoneyFailureOrPendingDescription.set(it.description)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTransferPendingFragment)
                    } else {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(false)
                        mActivityViewModel.isTransactionFailed.set(true)
                        mActivityViewModel.sendMoneyFailureOrPendingDescription.set(it.description)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTransferPendingFragment)
                    }
                }
            })

        mActivityViewModel.getMerchantPaymentResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    mActivityViewModel.transactionID = it.transactionId
                    // (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                    DialogUtils.successFailureDialogue( activity as SendMoneyActivity,it.description,0,object :DialogUtils.OnYesClickListner{
                        override fun onDialogYesClickListner() {
                            mActivityViewModel.isUserRegistered.set(false)
                    mActivityViewModel.isFundTransferUseCase.set(false)
                    mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    (activity as SendMoneyActivity).startNewActivityAndClear(activity as SendMoneyActivity,MainActivity::class.java)
                        }
                    })
                }else if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity,it.description)
                } else {
                    if (it.responseCode.equals(ApiConstant.API_PENDING)) {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(true)
                        mActivityViewModel.isTransactionFailed.set(false)
                        mActivityViewModel.sendMoneyFailureOrPendingDescription.set(it.description)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTransferPendingFragment)
                    } else {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(false)
                        mActivityViewModel.isTransactionFailed.set(true)
                        mActivityViewModel.sendMoneyFailureOrPendingDescription.set(it.description)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTransferPendingFragment)
                    }
                }
            })

        mActivityViewModel.getPaymentResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceafter
                    mActivityViewModel.transactionID = it.transactionId
                    //  (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                    DialogUtils.successFailureDialogue(activity as SendMoneyActivity,it.description,0,object :DialogUtils.OnYesClickListner{
                        override fun onDialogYesClickListner() {
                            mActivityViewModel.isUserRegistered.set(false)
                            mActivityViewModel.isFundTransferUseCase.set(false)
                            mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
                            Constants.HEADERS_FOR_PAYEMNTS = false
                            (activity as SendMoneyActivity).startNewActivityAndClear(activity as SendMoneyActivity,MainActivity::class.java)
                        }
                    })
                } else if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity, it.description)
                } else {
                    if (it.responseCode.equals(ApiConstant.API_PENDING)) {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(true)
                        mActivityViewModel.isTransactionFailed.set(false)
                        mActivityViewModel.sendMoneyFailureOrPendingDescription.set(it.description)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTransferPendingFragment)
                    } else {
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(false)
                        mActivityViewModel.isTransactionFailed.set(true)
                        mActivityViewModel.sendMoneyFailureOrPendingDescription.set(it.description)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTransferPendingFragment)
                    }
                }
            })
    }

    private fun updateUI() {
//        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//        tvAmountVal == AmountTotal

        mDataBinding.tvSenderNameVal.text =
            Constants.balanceInfoAndResponse?.firstname +" "+ Constants.balanceInfoAndResponse?.surname
        mDataBinding.tvSenderNumberVal.text = Constants.CURRENT_USER_MSISDN

        mDataBinding.tvCompanyNameVal.text = mActivityViewModel.transferdAmountTo
        var ReceiverName =
            mActivityViewModel.mAccountHolderInfoResponseObserver.get()?.firstName + " " + mActivityViewModel.mAccountHolderInfoResponseObserver.get()?.sureName
        mActivityViewModel.ReceiverName = ReceiverName
        mDataBinding.tvOwnerNameVal.text = ReceiverName
        mDataBinding.tvReceiptCodeVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.amountToTransfer
       var fee=mActivityViewModel.feeAmount.toDouble()+mActivityViewModel.totalTax
          fee= String.format("%.2f", fee).toDouble()
        mDataBinding.tvDHVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + fee

        amountToTransfer = Constants.addAmountAndFee(
            mActivityViewModel.amountToTransfer.toDouble(),
            mActivityViewModel.feeAmount.toDouble()
        )
        if(amountToTransfer.isNullOrEmpty())
        {
            amountToTransfer="0"
        }
        var totalCost=amountToTransfer.toDouble()+mActivityViewModel.totalTax
        totalCost= String.format("%.2f", totalCost).toDouble()
        mDataBinding.tvAmountVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + totalCost

        if (mActivityViewModel.isUserRegistered!=null && mActivityViewModel.isUserRegistered.get()!=null && mActivityViewModel.isUserRegistered.get()!!) {
            mDataBinding.tvOwnerNameTitle.visibility = View.VISIBLE
            mDataBinding.tvOwnerNameVal.visibility = View.VISIBLE
            mDataBinding.divider2.visibility = View.VISIBLE

        } else {
            mDataBinding.tvOwnerNameTitle.visibility = View.GONE
            mDataBinding.tvOwnerNameVal.visibility = View.GONE
            mDataBinding.divider2.visibility = View.GONE
        }

        mDataBinding.receiverNameGroup.visibility = View.GONE

        if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
            mDataBinding.tvDHTitle.visibility = View.GONE
            mDataBinding.tvDHVal.visibility = View.GONE
        }
        if(Constants.IS_AGENT_USER)
        {
            mDataBinding.tvDHTitle.visibility=View.GONE
            mDataBinding.tvDHVal.visibility=View.GONE
        }
    }

    override fun onNextClickListner(view: View) {
        DialogUtils.showPasswordDialoge(activity as SendMoneyActivity,
            object : DialogUtils.OnPasswordDialogClickListner {
                override fun onDialogYesClickListner(password: String) {
                    Constants.HEADERS_FOR_PAYEMNTS = true
                    Constants.CURRENT_USER_CREDENTIAL = password
                    if (Constants.IS_AGENT_USER) {
                        if (mActivityViewModel.isFundTransferUseCase.get()!!) {
                            mActivityViewModel.requestForFloatTransferApi(
                                activity,
                                mActivityViewModel.qouteId
                            )
                        }

                        if (mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!) {
                            if (mActivityViewModel.isUserRegistered!=null && mActivityViewModel.isUserRegistered.get()!=null && mActivityViewModel.isUserRegistered.get()!!) {
                                if (mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!) {
                                    mActivityViewModel.requestFoMerchantApi(
                                        activity,
                                        Constants.CURRENT_USER_MSISDN,
                                        mActivityViewModel.qouteId
                                    )
                                }
                            } else {
                                if (mActivityViewModel.isAccountHolderInformationFailed.get()!!) {
                                    if(mActivityViewModel.isFundTransferUseCase.get()!!){
                                        mActivityViewModel.requestForSimplePayementApi(
                                            activity,
                                            mActivityViewModel.qouteId,
                                            Constants.CURRENT_USER_MSISDN,
                                            Constants.PAYMENT_TYPE_SEND_MONEY
                                        )
                                    } else if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                                        mActivityViewModel.requestForSimplePayementApi(
                                            activity,
                                            mActivityViewModel.qouteId,
                                            Constants.CURRENT_USER_MSISDN,
                                            Constants.PAYMENT_TYPE_INITIATE_MERCHANT
                                        )
                                    }
                                } else {
                                    if(mActivityViewModel.isFundTransferUseCase.get()!!){
                                        mActivityViewModel.requestFoPayementApi(
                                            activity,
                                            mActivityViewModel.qouteId,
                                            Constants.CURRENT_USER_MSISDN,
                                            Constants.PAYMENT_TYPE_SEND_MONEY
                                        )
                                    } else if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                                        mActivityViewModel.requestFoPayementApi(
                                            activity,
                                            mActivityViewModel.qouteId,
                                            Constants.CURRENT_USER_MSISDN,
                                            Constants.PAYMENT_TYPE_INITIATE_MERCHANT
                                        )
                                    }
                                }
                            }
                        }

                    } else {
                        if ( mActivityViewModel.isUserRegistered.get()!=null && mActivityViewModel.isUserRegistered.get()!!) {
                            if (mActivityViewModel.isFundTransferUseCase.get()!!) {
                                mActivityViewModel.requestFoTransferApi(
                                    activity,
                                    mActivityViewModel.qouteId
                                )
                            }

                            if (mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!) {
                                mActivityViewModel.requestFoMerchantApi(
                                    activity,
                                    Constants.CURRENT_USER_MSISDN,
                                    mActivityViewModel.qouteId
                                )
                            }
                        } else {
                            if (mActivityViewModel.isAccountHolderInformationFailed.get()!!) {
                                if(mActivityViewModel.isFundTransferUseCase.get()!!){
                                    mActivityViewModel.requestForSimplePayementApi(
                                        activity,
                                        mActivityViewModel.qouteId,
                                        Constants.CURRENT_USER_MSISDN,
                                        Constants.PAYMENT_TYPE_SEND_MONEY
                                    )
                                } else if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                                    mActivityViewModel.requestForSimplePayementApi(
                                        activity,
                                        mActivityViewModel.qouteId,
                                        Constants.CURRENT_USER_MSISDN,
                                        Constants.PAYMENT_TYPE_INITIATE_MERCHANT
                                    )
                                }
                            } else {
                                if(mActivityViewModel.isFundTransferUseCase.get()!!){
                                    mActivityViewModel.requestFoPayementApi(
                                        activity,
                                        mActivityViewModel.qouteId,
                                        Constants.CURRENT_USER_MSISDN,
                                        Constants.PAYMENT_TYPE_SEND_MONEY
                                    )
                                } else if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                                    mActivityViewModel.requestFoPayementApi(
                                        activity,
                                        mActivityViewModel.qouteId,
                                        Constants.CURRENT_USER_MSISDN,
                                        Constants.PAYMENT_TYPE_INITIATE_MERCHANT
                                    )
                                }
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
        (activity as SendMoneyActivity).startNewActivityAndClear(
            activity as SendMoneyActivity,
            MainActivity::class.java
        )
    }

}