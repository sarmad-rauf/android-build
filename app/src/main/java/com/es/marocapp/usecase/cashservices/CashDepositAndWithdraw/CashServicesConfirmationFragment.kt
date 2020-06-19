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
            if(mActivityViewModel.isOTPFlow.get()!!){
                (activity as CashServicesActivity).navController.popBackStack(R.id.cashServicesVerifyOtpFragment,false)
            }else{
                (activity as CashServicesActivity).navController.popBackStack(R.id.cashMsisdnAndAmountFragment,false)
            }
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
        mDataBinding.btnConfirmationPay.text = mActivityViewModel.trasferTypeSelected.get()!!
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
                }else{
                    if(it.responseCode.equals(ApiConstant.API_PENDING)){
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesConfirmationFragment_to_cashServicesPendingFragment)
                    }else{
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
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
                }else{
                    if(it.responseCode.equals(ApiConstant.API_PENDING)){
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesConfirmationFragment_to_cashServicesPendingFragment)
                    }else{
                        Constants.HEADERS_FOR_PAYEMNTS = false
                        DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
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
        mDataBinding.tvCompanyNameVal.text = mActivityViewModel.transferdAmountTo
        mDataBinding.tvReceiptCodeVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.amountToTransfer
        mDataBinding.tvDHVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.feeAmount

        amountToTransfer = Constants.addAmountAndFee(mActivityViewModel.amountToTransfer.toDouble(),mActivityViewModel.feeAmount.toDouble())
        mDataBinding.tvAmountVal.text =Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+amountToTransfer


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
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as CashServicesActivity).startNewActivityAndClear(activity as CashServicesActivity,
            MainActivity::class.java)
    }

}