package com.es.marocapp.usecase.cashservices.CashDepositAndWithdraw

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashServiceConfirmationBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.cashservices.CashServicesClickListner
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger


class CashServicesConfirmationFragment : BaseFragment<FragmentCashServiceConfirmationBinding>(),
    CashServicesClickListner {

    private lateinit var mActivityViewModel : CashServicesViewModel

    private var amountToTransfer = ""

    override fun setLayout(): Int {
        return R.layout.fragment_cash_service_confirmation
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as CashServicesActivity).get(CashServicesViewModel::class.java)
        mDataBinding.apply {
            listner = this@CashServicesConfirmationFragment
            viewmodel = mActivityViewModel
        }

        mDataBinding.imgBackButton.setOnClickListener {
            (activity as CashServicesActivity).navController.popBackStack(R.id.cashMsisdnAndAmountFragment,false)

        }

        (activity as CashServicesActivity).setHeaderVisibility(false)
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
//        mDataBinding.btnConfirmationPay.text = mActivityViewModel.trasferTypeSelected.get()!! // before it is showing as deposit and Withdraw in button text on base of use case

      if(mActivityViewModel.isWithdrawUseCase.get()!!) {
          mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_Confirm")
      }
        else {
          mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_Validate")
        }
        Logger.debugLog("abro","conform  ${LanguageData.getStringValue("BtnTitle_Confirm")}  validate ${LanguageData.getStringValue("BtnTitle_Validate")}")
        mDataBinding.tvSendNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.tvSendNumberTitle.text = LanguageData.getStringValue("SenderNumber")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@CashServicesConfirmationFragment, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivityViewModel.getInitiateTrasnferResponseListner.observe(this@CashServicesConfirmationFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.transactionID = it.financialTransactionId
                    mActivityViewModel.requestForGetBalanceApi(activity)
                }else if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity,it.description)
                }else{
                    if(it.responseCode.equals(ApiConstant.API_PENDING)){
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(true)
                        mActivityViewModel.isTransactionFailed.set(false)
                        mActivityViewModel.cashServiceFailureOrPendingDescription.set(it.description)
                        (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesConfirmationFragment_to_cashServicesPendingFragment)
                    }else{
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(false)
                        mActivityViewModel.isTransactionFailed.set(true)
                        mActivityViewModel.cashServiceFailureOrPendingDescription.set(it.description)
                        (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesConfirmationFragment_to_cashServicesPendingFragment)
                    }
                }
            }
        )

        mActivityViewModel.getCashInWithOtpResponseListner.observe(this@CashServicesConfirmationFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    mActivityViewModel.transactionID = it.financialTransactionId
                    (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesConfirmationFragment_to_cashServicesSuccessFragment)
                }else if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity,it.description)
                }else{
                    if(it.responseCode.equals(ApiConstant.API_PENDING)){
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(true)
                        mActivityViewModel.isTransactionFailed.set(false)
                        mActivityViewModel.cashServiceFailureOrPendingDescription.set(it.description)
                        (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesConfirmationFragment_to_cashServicesPendingFragment)
                    }else{
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        mActivityViewModel.isTransactionPending.set(false)
                        mActivityViewModel.isTransactionFailed.set(true)
                        mActivityViewModel.cashServiceFailureOrPendingDescription.set(it.description)
                        (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesConfirmationFragment_to_cashServicesPendingFragment)
                    }
                }
            }
        )

        mActivityViewModel.getBalanceResponseListner.observe(this@CashServicesConfirmationFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    mActivityViewModel.senderBalanceAfter = it.amount.toString()
                    (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesConfirmationFragment_to_cashServicesSuccessFragment)
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            })
    }

    private fun updateUI() {

        mDataBinding.tvSenderNameVal.text = Constants.balanceInfoAndResponse?.firstname +" "+ Constants.balanceInfoAndResponse?.surname
        mDataBinding.tvSenderNumberVal.text = Constants.CURRENT_USER_MSISDN

        mDataBinding.tvCompanyNameVal.text = mActivityViewModel.transferdAmountTo
        mDataBinding.tvReceiptCodeVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.amountToTransfer
        mDataBinding.tvDHVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.feeAmount

        amountToTransfer = Constants.addAmountAndFee(mActivityViewModel.amountToTransfer.toDouble(),mActivityViewModel.feeAmount.toDouble())
        mDataBinding.tvAmountVal.text =Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+amountToTransfer

        mDataBinding.receiverNameGroup.visibility = View.GONE


    }

    override fun onNextClickListner(view: View) {
        DialogUtils.showPasswordDialoge(activity,object : DialogUtils.OnPasswordDialogClickListner{
            override fun onDialogYesClickListner(password: String) {
                Constants.HEADERS_FOR_PAYEMNTS = true
                Constants.CURRENT_USER_CREDENTIAL = password

                if(mActivityViewModel.isWithdrawUseCase.get()!!){
                    mActivityViewModel.requestForInitiateTransferApi(activity,mActivityViewModel.qouteId,mActivityViewModel.noteToSend)
                }

                if(mActivityViewModel.isDepositUseCase.get()!!){
                    mActivityViewModel.requestForCashInWithOtpApi(activity,mActivityViewModel.qouteId,mActivityViewModel.mOTP)
                }
            }

        })
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isDepositUseCase.set(false)
        mActivityViewModel.isWithdrawUseCase.set(false)
        mActivityViewModel.isOTPFlow.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as CashServicesActivity).startNewActivityAndClear(activity as CashServicesActivity,
            MainActivity::class.java)
    }

}