package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferSuccessBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

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

        mDataBinding.addToFavoriteCheckBox.setOnClickListener {
            DialogUtils.showAddToFavoriteDialoge(activity as SendMoneyActivity,object : DialogUtils.OnAddToFavoritesDialogClickListner{
                override fun onDialogYesClickListner(nickName: String) {
                    mDataBinding.addToFavoriteCheckBox.isActivated = true
                    mDataBinding.addToFavoriteCheckBox.isChecked = true
                    mDataBinding.addToFavoriteCheckBox.isClickable = false

                    mActivityViewModel.requestForAddFavoritesApi(activity,nickName)
                }

                override fun onDialogNoClickListner() {
                    mDataBinding.addToFavoriteCheckBox.isActivated = false
                    mDataBinding.addToFavoriteCheckBox.isChecked = false
                    mDataBinding.addToFavoriteCheckBox.isClickable = true
                }

            })
        }

        if(mActivityViewModel.isUserSelectedFromFavorites.get()!!){
            mDataBinding.addToFavoriteCheckBox.isActivated = true
            mDataBinding.addToFavoriteCheckBox.isChecked = true
            mDataBinding.addToFavoriteCheckBox.isClickable = false
        }else{
            mDataBinding.addToFavoriteCheckBox.isActivated = false
            mDataBinding.addToFavoriteCheckBox.isChecked = false
            mDataBinding.addToFavoriteCheckBox.isClickable = true
        }

        (activity as SendMoneyActivity).setHeaderVisibility(false)
        setStrings()
        updateUI()
        subscribeObserver()

    }

    private fun subscribeObserver() {
        mActivityViewModel.getAddFavoritesResponseListner.observe(this@FundsTrasnferSuccessFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.contactList.isNullOrEmpty()){
                        Constants.mContactListArray.clear()
                        Constants.mContactListArray.addAll(it.contactList)
                        DialogUtils.showErrorDialoge(activity,it.description)
                    }
                }else{
                    mDataBinding.addToFavoriteCheckBox.isActivated = false
                    mDataBinding.addToFavoriteCheckBox.isChecked = false
                    mDataBinding.addToFavoriteCheckBox.isClickable = true
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            })
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
        mDataBinding.addToFavoritesTitle.text = LanguageData.getStringValue("BtnTitle_AddToFavorites")

        mDataBinding.tvSendNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.tvSendNumberTitle.text = LanguageData.getStringValue("SenderNumber")
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

        if(mActivityViewModel.isUserRegistered!=null && mActivityViewModel.isUserRegistered.get()!=null && mActivityViewModel.isUserRegistered.get()!!){
            mDataBinding.tvContactNumTitle.visibility = View.VISIBLE
            mDataBinding.tvContactNumVal.visibility = View.VISIBLE
        }else{
            mDataBinding.tvContactNumTitle.visibility = View.GONE
            mDataBinding.tvContactNumVal.visibility = View.GONE
        }

        mDataBinding.tvSenderNameVal.text = Constants.balanceInfoAndResponse.firstname + Constants.balanceInfoAndResponse.surname
        mDataBinding.tvSenderNumberVal.text = Constants.CURRENT_USER_MSISDN


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