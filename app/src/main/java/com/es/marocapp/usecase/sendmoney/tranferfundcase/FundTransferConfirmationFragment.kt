package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferConfirmationBinding
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
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
        updateUI()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FundTransferConfirmationFragment, Observer {
            Constants.HEADERS_FOR_PAYEMNTS = false
            DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it)
        })
        mActivityViewModel.getTransferResponseListner.observe(this@FundTransferConfirmationFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                }else{
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
        mDataBinding.tvReceiptCodeVal.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.amountToTransfer
        mDataBinding.tvDHVal.text = Constants.CURRENT_CURRENCY_TYPE+" "+mActivityViewModel.feeAmount

        amountToTransfer = Constants.addAmountAndFee(mActivityViewModel.amountToTransfer.toDouble(),mActivityViewModel.feeAmount.toDouble())
        mDataBinding.tvAmountVal.text =Constants.CURRENT_CURRENCY_TYPE+" "+amountToTransfer

    }

    override fun onNextClickListner(view: View) {
        DialogUtils.showPasswordDialoge(activity as SendMoneyActivity,object : DialogUtils.OnPasswordDialogClickListner{
            override fun onDialogYesClickListner(password: String) {
                Constants.HEADERS_FOR_PAYEMNTS = true
                Constants.CURRENT_USER_CREDENTIAL = password
                if(mActivityViewModel.isUserRegistered.get()!!){
                    mActivityViewModel.requestFoTransferApi(activity,mActivityViewModel.qouteId)
                }else{
                    mActivityViewModel.requestFoPayementApi(activity,mActivityViewModel.qouteId,Constants.CURRENT_USER_MSISDN)
                }
            }
        })
    }

    override fun onBackClickListner(view: View) {
    }

}