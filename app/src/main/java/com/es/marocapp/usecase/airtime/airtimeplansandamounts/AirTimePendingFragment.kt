package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentAirTimePendingBinding
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

class AirTimePendingFragment : BaseFragment<FragmentAirTimePendingBinding>(), AirTimeClickListner {

    private lateinit var mActivityViewModel: AirTimeViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_air_time_pending
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(AirTimeViewModel::class.java)
        mDataBinding.apply {
            listner = this@AirTimePendingFragment
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

        (activity as AirTimeActivity).setHeaderVisibility(false)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(false)

        setStrings()
        updateUI()
        subscribeObserver()
    }

    private fun updateUI() {
        mDataBinding.descriptionOfTransaction.text = mActivityViewModel.airTimeResponse.get()!!.description

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

        if(mActivityViewModel.isQuickRechargeUseCase.get()!!){
            mDataBinding.favoritesGroup.visibility = View.GONE
        }
    }

    private fun setStrings() {
        mDataBinding.tvSuccessTitle.text = mActivityViewModel.airTimeSelected.get()!!

        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_OK")

        mDataBinding.addToFavoritesTitle.text = LanguageData.getStringValue("BtnTitle_AddToFavorites")

        mDataBinding.successTItle.text = LanguageData.getStringValue("Pending")
        mDataBinding.successTItleFailed.text = LanguageData.getStringValue("Failed")
    }

    private fun subscribeObserver() {
        mActivityViewModel.getAddFavoritesResponseListner.observe(this@AirTimePendingFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    Constants.mContactListArray.clear()
                    Constants.mContactListArray.addAll(it.contactList)
                    DialogUtils.showErrorDialoge(activity,it.description)
                }else{
                    mDataBinding.addToFavoriteCheckBox.isActivated = false
                    mDataBinding.addToFavoriteCheckBox.isChecked = false
                    mDataBinding.addToFavoriteCheckBox.isClickable = true
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            })
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