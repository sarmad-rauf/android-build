package com.es.marocapp.usecase.cashservices.CashDepositAndWithdraw

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashServicesPendingBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.cashservices.CashServicesClickListner
import com.es.marocapp.usecase.cashservices.CashServicesViewModel

class CashServicesPendingFragment : BaseFragment<FragmentCashServicesPendingBinding>(),
    CashServicesClickListner {

    private lateinit var mActivityViewModel: CashServicesViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_cash_services_pending
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as CashServicesActivity).get(CashServicesViewModel::class.java)
        mDataBinding.apply {
            listner = this@CashServicesPendingFragment
            viewmodel = mActivityViewModel
        }

        mActivityViewModel.popBackStackTo = R.id.cashServicesConfirmationFragment

        (activity as CashServicesActivity).setHeaderVisibility(false)
        setStrings()
        updateUI()

    }

    private fun updateUI() {
        mDataBinding.descriptionOfTransaction.text = mActivityViewModel.cashServiceFailureOrPendingDescription.get()!!

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

    private fun setStrings() {
        mDataBinding.tvSuccessTitle.text = mActivityViewModel.trasferTypeSelected.get()!!

        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_OK")

        mDataBinding.addToFavoritesTitle.text = LanguageData.getStringValue("BtnTitle_AddToFavorites")

        mDataBinding.successTItle.text = LanguageData.getStringValue("Pending")
        mDataBinding.successTItleFailed.text = LanguageData.getStringValue("Failed")
    }

    override fun onNextClickListner(view: View) {
        mActivityViewModel.isDepositUseCase.set(false)
        mActivityViewModel.isWithdrawUseCase.set(false)
        (activity as CashServicesActivity).startNewActivityAndClear(
            activity as CashServicesActivity,
            MainActivity::class.java
        )
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isDepositUseCase.set(false)
        mActivityViewModel.isWithdrawUseCase.set(false)
        (activity as CashServicesActivity).startNewActivityAndClear(
            activity as CashServicesActivity,
            MainActivity::class.java
        )
    }

}