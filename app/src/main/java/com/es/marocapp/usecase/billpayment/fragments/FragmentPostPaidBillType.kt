package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel

class FragmentPostPaidBillType : BaseFragment<FragmentBillPaymentTypeBinding>() {

    private lateinit var mActivityViewModel : BillPaymentViewModel

    private lateinit var mBillPaymentItemTypeAdapter: PaymentItemsAdapter
    private var mBillPaymentTypes: ArrayList<String> = ArrayList()
    private var mBillPaymentTypesIcon: ArrayList<Int>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as BillPaymentActivity).get(
            BillPaymentViewModel::class.java
        )
        mDataBinding.apply {
        }

        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("BillType")
        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )

        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        mActivityViewModel.popBackStackTo = R.id.fragmentPostPaidServiceProvider

        mBillPaymentTypes.apply {
            add(LanguageData.getStringValue("PostpaidMobile").toString())
            add(LanguageData.getStringValue("PostpaidFix").toString())
            add(LanguageData.getStringValue("Internet").toString())
        }

        mBillPaymentTypesIcon.apply {
            add(R.drawable.postpaid)
            add(R.drawable.postpaid_fix)
            add(R.drawable.internet)
        }

        mBillPaymentItemTypeAdapter = PaymentItemsAdapter(mBillPaymentTypes, mBillPaymentTypesIcon,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                when(paymentItems){
                    LanguageData.getStringValue("PostpaidMobile") -> {
                        mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("PostpaidMobile"))
                        mActivityViewModel.billTypeSelectedIcon = R.drawable.postpaid
                        mActivityViewModel.isPostPaidMobileSelected.set(true)
                        mActivityViewModel.isPostPaidFixSelected.set(false)
                        mActivityViewModel.isInternetSelected.set(false)
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillType_to_fragmentBillPaymentMsisdn)
                    }
                    LanguageData.getStringValue("PostpaidFix") -> {
                        mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("PostpaidFix"))
                        mActivityViewModel.billTypeSelectedIcon = R.drawable.postpaid_fix
                        mActivityViewModel.isPostPaidMobileSelected.set(false)
                        mActivityViewModel.isPostPaidFixSelected.set(true)
                        mActivityViewModel.isInternetSelected.set(false)
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillType_to_fragmentBillPaymentMsisdn)
                    }
                    LanguageData.getStringValue("Internet") -> {
                        mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("Internet"))
                        mActivityViewModel.billTypeSelectedIcon = R.drawable.internet
                        mActivityViewModel.isPostPaidMobileSelected.set(false)
                        mActivityViewModel.isPostPaidFixSelected.set(false)
                        mActivityViewModel.isInternetSelected.set(true)
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillType_to_fragmentBillPaymentMsisdn)
                    }
                    else -> Toast.makeText(activity,"Nothing Clicked Clicked", Toast.LENGTH_SHORT).show()

                }
            }

        })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mBillPaymentItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as BillPaymentActivity)
        }

    }

}