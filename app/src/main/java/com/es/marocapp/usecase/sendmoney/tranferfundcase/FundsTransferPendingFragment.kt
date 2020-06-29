package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferPendingBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FundsTransferPendingFragment : BaseFragment<FragmentFundsTransferPendingBinding>(),
    FundsTrasnferClickLisntener {

    private lateinit var mActivityViewModel: SendMoneyViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_funds_transfer_pending
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listner = this@FundsTransferPendingFragment
            viewmodel = mActivityViewModel
        }

        (activity as SendMoneyActivity).setHeaderVisibility(false)

        mDataBinding.addToFavoriteCheckBox.setOnClickListener {
            DialogUtils.showAddToFavoriteDialoge(activity as SendMoneyActivity,
                object : DialogUtils.OnAddToFavoritesDialogClickListner {
                    override fun onDialogYesClickListner(nickName: String) {
                        mDataBinding.addToFavoriteCheckBox.isActivated = true
                        mDataBinding.addToFavoriteCheckBox.isChecked = true
                        mDataBinding.addToFavoriteCheckBox.isClickable = false

                        mActivityViewModel.requestForAddFavoritesApi(activity, nickName)
                    }

                    override fun onDialogNoClickListner() {
                        mDataBinding.addToFavoriteCheckBox.isActivated = false
                        mDataBinding.addToFavoriteCheckBox.isChecked = false
                        mDataBinding.addToFavoriteCheckBox.isClickable = true
                    }

                })
        }

        if (mActivityViewModel.isUserSelectedFromFavorites.get()!!) {
            mDataBinding.addToFavoriteCheckBox.isActivated = true
            mDataBinding.addToFavoriteCheckBox.isChecked = true
            mDataBinding.addToFavoriteCheckBox.isClickable = false
        } else {
            mDataBinding.addToFavoriteCheckBox.isActivated = false
            mDataBinding.addToFavoriteCheckBox.isChecked = false
            mDataBinding.addToFavoriteCheckBox.isClickable = true
        }

        setStrings()
        updateUI()
        subscribeObserver()
    }

    private fun updateUI() {
        mDataBinding.descriptionOfTransaction.text = mActivityViewModel.sendMoneyFailureOrPendingDescription.get()!!

        if(mActivityViewModel.isTransactionFailed.get()!!){
            mDataBinding.imgSuccess.setImageResource(R.drawable.img_payment_failure)
            mDataBinding.successTItle.visibility = View.GONE
            mDataBinding.successTItleFailed.visibility = View.VISIBLE
            mDataBinding.favoritesGroup.visibility = View.GONE
        }

        if(mActivityViewModel.isTransactionPending.get()!!){
            mDataBinding.imgSuccess.setImageResource(R.drawable.img_payment_pending)
            mDataBinding.successTItle.visibility = View.VISIBLE
            mDataBinding.successTItleFailed.visibility = View.GONE
            mDataBinding.favoritesGroup.visibility = View.VISIBLE
        }
    }

    private fun subscribeObserver() {
        mActivityViewModel.getAddFavoritesResponseListner.observe(this@FundsTransferPendingFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.mContactListArray.clear()
                    Constants.mContactListArray.addAll(it.contactList)
                    DialogUtils.showErrorDialoge(activity, it.description)
                } else {
                    mDataBinding.addToFavoriteCheckBox.isActivated = false
                    mDataBinding.addToFavoriteCheckBox.isChecked = false
                    mDataBinding.addToFavoriteCheckBox.isClickable = true
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

    private fun setStrings() {

        mDataBinding.tvSuccessTitle.text = mActivityViewModel.trasferTypeSelected.get()!!
        mDataBinding.successTItle.text = LanguageData.getStringValue("Pending")

        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_OK")

        mDataBinding.addToFavoritesTitle.text =
            LanguageData.getStringValue("BtnTitle_AddToFavorites")
        mDataBinding.successTItleFailed.text = LanguageData.getStringValue("Failed")

    }

    override fun onNextClickListner(view: View) {
        mActivityViewModel.isUserRegistered.set(false)
        mActivityViewModel.isFundTransferUseCase.set(false)
        mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as SendMoneyActivity).startNewActivityAndClear(
            activity as SendMoneyActivity,
            MainActivity::class.java
        )
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