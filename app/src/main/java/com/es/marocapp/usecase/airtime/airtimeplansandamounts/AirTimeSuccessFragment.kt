package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentAirTimeSuccessLayoutBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.airtime.AirTimeClickListner
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class AirTimeSuccessFragment : BaseFragment<FragmentAirTimeSuccessLayoutBinding>(),
    AirTimeClickListner {

    private lateinit var mActivityViewModel: AirTimeViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_air_time_success_layout
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(AirTimeViewModel::class.java)
        mDataBinding.apply {
            listner = this@AirTimeSuccessFragment
            viewmodel = mActivityViewModel
        }

        mDataBinding.addToFavoriteCheckBox.setOnClickListener {
            DialogUtils.showAddToFavoriteDialoge(activity as AirTimeActivity,object : DialogUtils.OnAddToFavoritesDialogClickListner{
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

        if(!mActivityViewModel.isQuickRechargeUseCase.get()!!){
            if(mActivityViewModel.isUserSelectedFromFavorites.get()!!){
                mDataBinding.addToFavoriteCheckBox.isActivated = true
                mDataBinding.addToFavoriteCheckBox.isChecked = true
                mDataBinding.addToFavoriteCheckBox.isClickable = false
            }else{
                mDataBinding.addToFavoriteCheckBox.isActivated = false
                mDataBinding.addToFavoriteCheckBox.isChecked = false
                mDataBinding.addToFavoriteCheckBox.isClickable = true
            }
        }

        if(mActivityViewModel.isQuickRechargeUseCase.get()!!){
            mDataBinding.favoritesGroup.visibility = View.GONE
        }

        (activity as AirTimeActivity).setHeaderVisibility(false)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(false)

        setStrings()
        updateUI()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        mActivityViewModel.getAddFavoritesResponseListner.observe(this@AirTimeSuccessFragment,
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
        mDataBinding.tvOwnerNameTitle2.text = LanguageData.getStringValue("Bill")
        mDataBinding.tvContactNumTitle2.text = LanguageData.getStringValue("Fee")
        mDataBinding.tvDHTitle.text = LanguageData.getStringValue("Amount")
        mDataBinding.newBalanceTitle.text = LanguageData.getStringValue("YourNewBalanceIs")

        mDataBinding.tvSuccessTitle.text = mActivityViewModel.airTimeSelected.get()!!
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

        mDataBinding.tvSenderNameVal.text = Constants.balanceInfoAndResponse?.firstname +" "+ Constants.balanceInfoAndResponse?.surname
        mDataBinding.tvSenderNumberVal.text = Constants.CURRENT_USER_MSISDN

        mDataBinding.tvOwnerNameVal.text = mActivityViewModel.transactionID
//        mDataBinding.tvContactNumVal.text = mActivityViewModel.ReceiverName
        mDataBinding.tvReceiverNumberVal.text = mActivityViewModel.transferdAmountTo

        mDataBinding.tvOwnerNameVal2.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.amountToTransfer
        mDataBinding.tvContactNumVal2.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.feeAmount

        var amountToTransfer = Constants.addAmountAndFee(mActivityViewModel.amountToTransfer.toDouble(),mActivityViewModel.feeAmount.toDouble())
        mDataBinding.tvDHVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+amountToTransfer

        Constants.balanceInfoAndResponse?.balance = mActivityViewModel.senderBalanceAfter

        mDataBinding.newBalanceVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+mActivityViewModel.senderBalanceAfter

        mDataBinding.receiverNameGroup.visibility = View.GONE
    }

    override fun onNextBtnClickListner(view: View) {
        mActivityViewModel.isRechargeFixeUseCase.set(false)
        mActivityViewModel.isRechargeMobileUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as AirTimeActivity).startNewActivityAndClear(activity as AirTimeActivity,
            MainActivity::class.java)
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isRechargeFixeUseCase.set(false)
        mActivityViewModel.isRechargeMobileUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as AirTimeActivity).startNewActivityAndClear(activity as AirTimeActivity,
            MainActivity::class.java)
    }

}