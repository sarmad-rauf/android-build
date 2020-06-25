package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.DialogUtils

class FragmentPostPaidServiceProvider : BaseFragment<FragmentBillPaymentTypeBinding>() {

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

        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("ServiceProvider")
        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )

        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        mActivityViewModel.popBackStackTo = R.id.fragmentPostPaidPaymentTypes

        mBillPaymentTypes.apply {
            if(mActivityViewModel.isBillUseCaseSelected.get()!!){
                add(LanguageData.getStringValue("IAM").toString())
            }
            if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
                add(LanguageData.getStringValue("Fatourati").toString())
            }
        }

        mBillPaymentTypesIcon.apply {
            if(mActivityViewModel.isBillUseCaseSelected.get()!!){
                add(R.drawable.iam)
            }
            if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
                add(R.drawable.fatourati)
            }
        }

        mBillPaymentItemTypeAdapter = PaymentItemsAdapter(mBillPaymentTypes, mBillPaymentTypesIcon,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                when(paymentItems){
                    LanguageData.getStringValue("IAM") -> {
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidServiceProvider_to_fragmentPostPaidBillType)
                    }

                    LanguageData.getStringValue("Fatourati") -> {
                        mActivityViewModel.requestForFatoratiStepOneApi(activity)
                    }

                    else -> Toast.makeText(activity,"Nothing Clicked Clicked", Toast.LENGTH_SHORT).show()

                }
            }

        })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mBillPaymentItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as BillPaymentActivity)
        }

        subscribeObserver()

    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FragmentPostPaidServiceProvider, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivityViewModel.getFatoratiStepOneResponseListner.observe(this@FragmentPostPaidServiceProvider,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidServiceProvider_to_fragmentPostPaidBillType)
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            })
    }

}