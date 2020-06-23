package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.BillDetailItemAdapter
import com.es.marocapp.adapter.PaidBillStatusAdpater
import com.es.marocapp.databinding.FragmentBillPaymentSuccessBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FragmentPostPaidBillPaymentSuccess : BaseFragment<FragmentBillPaymentSuccessBinding>(),
    BillPaymentClickListner {

    private lateinit var mActivityViewModel : BillPaymentViewModel

    private lateinit var mBillPaidAdapter : PaidBillStatusAdpater


    override fun setLayout(): Int {
       return R.layout.fragment_bill_payment_success
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            listner = this@FragmentPostPaidBillPaymentSuccess
            viewmodel = mActivityViewModel
        }

        mDataBinding.addToFavoriteCheckBox.setOnClickListener {
            DialogUtils.showAddToFavoriteDialoge(activity as BillPaymentActivity,object : DialogUtils.OnAddToFavoritesDialogClickListner{
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

        (activity as BillPaymentActivity).setHeaderVisibility(false)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        mDataBinding.tvSuccessTitle.text = LanguageData.getStringValue("BillPayment")

        populatePaidBillList()

        subscribeObserver()
    }

    private fun populatePaidBillList() {
        var receiverNumber = mActivityViewModel.transferdAmountTo.substringBefore("@")
        receiverNumber = receiverNumber.substringBefore("/")
        mBillPaidAdapter = PaidBillStatusAdpater(mActivityViewModel.listOfPostPaidBillPayment,mActivityViewModel.listOfSelectedBillAmount,mActivityViewModel.listOfSelectedBillFee,
            receiverNumber,mActivityViewModel.ReceiverName)

        mDataBinding.mPaidBillsRecycler.apply {
            adapter = mBillPaidAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun subscribeObserver() {
        mActivityViewModel.getAddFavoritesResponseListner.observe(this@FragmentPostPaidBillPaymentSuccess,
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


    override fun onSubmitClickListner(view: View) {
        mActivityViewModel.isPostPaidMobileSelected.set(false)
        mActivityViewModel.isPostPaidFixSelected.set(false)
        mActivityViewModel.isInternetSelected.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as BillPaymentActivity).startNewActivityAndClear(
            activity as BillPaymentActivity,
            MainActivity::class.java
        )
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isPostPaidMobileSelected.set(false)
        mActivityViewModel.isPostPaidFixSelected.set(false)
        mActivityViewModel.isInternetSelected.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as BillPaymentActivity).startNewActivityAndClear(
            activity as BillPaymentActivity,
            MainActivity::class.java
        )
    }

}