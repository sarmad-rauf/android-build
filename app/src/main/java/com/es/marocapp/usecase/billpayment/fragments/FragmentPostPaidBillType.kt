package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FatoratiFirstLetterIconItemAdapter
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.Creancier
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel

class FragmentPostPaidBillType : BaseFragment<FragmentBillPaymentTypeBinding>() {

    private lateinit var mActivityViewModel : BillPaymentViewModel

    private lateinit var mBillPaymentItemTypeAdapter: PaymentItemsAdapter
    private lateinit var mFatoratiItemTypeAdapter: FatoratiFirstLetterIconItemAdapter
    private var mBillPaymentTypes: ArrayList<String> = ArrayList()
    private var mBillPaymentTypesIcon: ArrayList<Int>  = ArrayList()
    private var mFatoratiTypesList: ArrayList<Creancier>  = arrayListOf()

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

        mBillPaymentTypes.clear()
        mBillPaymentTypes.apply {
            if(mActivityViewModel.isBillUseCaseSelected.get()!!){
                add(LanguageData.getStringValue("PostpaidMobile").toString())
                add(LanguageData.getStringValue("PostpaidFix").toString())
                add(LanguageData.getStringValue("Internet").toString())
            }
        }

        mBillPaymentTypesIcon.clear()
        mBillPaymentTypesIcon.apply {
            add(R.drawable.postpaid_blue)
            add(R.drawable.postpaid_fix_blue)
            add(R.drawable.internet_blue)
        }

        mFatoratiTypesList.clear()
        mFatoratiTypesList.apply {
            if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
                var fatoratiType = mActivityViewModel.fatoratiStepOneObserver.get()!!.creanciers
                if(fatoratiType.isNotEmpty()){
                    for(i in fatoratiType.indices){
                        add(fatoratiType[i])
                    }
                }
            }
        }


        mFatoratiItemTypeAdapter = FatoratiFirstLetterIconItemAdapter(mFatoratiTypesList,object : FatoratiFirstLetterIconItemAdapter.AdapterItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: Creancier) {
                mActivityViewModel.fatoratiTypeSelected.set(paymentItems)
                (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillType_to_fragmentBillPaymentMsisdn)
            }

        })

        mBillPaymentItemTypeAdapter = PaymentItemsAdapter(mBillPaymentTypes, mBillPaymentTypesIcon,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                when(paymentItems){
                    LanguageData.getStringValue("PostpaidMobile") -> {
                        mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("PostpaidMobile"))
                        mActivityViewModel.billTypeSelectedIcon = R.drawable.postpaid_blue
                        mActivityViewModel.isPostPaidMobileSelected.set(true)
                        mActivityViewModel.isPostPaidFixSelected.set(false)
                        mActivityViewModel.isInternetSelected.set(false)
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillType_to_fragmentBillPaymentMsisdn)
                    }
                    LanguageData.getStringValue("PostpaidFix") -> {
                        mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("PostpaidFix"))
                        mActivityViewModel.billTypeSelectedIcon = R.drawable.postpaid_fix_blue
                        mActivityViewModel.isPostPaidMobileSelected.set(false)
                        mActivityViewModel.isPostPaidFixSelected.set(true)
                        mActivityViewModel.isInternetSelected.set(false)
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillType_to_fragmentBillPaymentMsisdn)
                    }
                    LanguageData.getStringValue("Internet") -> {
                        mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("Internet"))
                        mActivityViewModel.billTypeSelectedIcon = R.drawable.internet_blue
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
            if(mActivityViewModel.isBillUseCaseSelected.get()!!){
                adapter = mBillPaymentItemTypeAdapter
            }

            if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
                adapter = mFatoratiItemTypeAdapter
            }
            layoutManager = LinearLayoutManager(activity as BillPaymentActivity)
        }

    }

}