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
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel

class FragmentPostPaidPaymentTypes : BaseFragment<FragmentBillPaymentTypeBinding>() {

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

        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("PaymentType")
        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )

        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        mActivityViewModel.popBackStackTo = -1

        mBillPaymentTypes.apply {
            add(LanguageData.getStringValue("Bill").toString())
            add(LanguageData.getStringValue("Favorites").toString())
        }

        mBillPaymentTypesIcon.apply {
            add(R.drawable.bill)
            add(R.drawable.favorites)
        }

        mBillPaymentItemTypeAdapter = PaymentItemsAdapter(mBillPaymentTypes, mBillPaymentTypesIcon,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                when(paymentItems){
                    LanguageData.getStringValue("Bill") -> {
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidPaymentTypes_to_fragmentPostPaidServiceProvider)
                    }
                    LanguageData.getStringValue("Favorites") -> {
                        Toast.makeText(activity,"Under Development",Toast.LENGTH_SHORT).show()
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