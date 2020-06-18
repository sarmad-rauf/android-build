package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.CustomizeIconsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.GetAirTimeUseCasesResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.utils.DialogUtils

class AirTimePlanFragment : BaseFragment<FragmentBillPaymentTypeBinding>() {

    lateinit var mActivityViewModel: AirTimeViewModel
    private lateinit var mAirTimePlansItemTypeAdapter: CustomizeIconsAdapter
    private var mAirTimePlansTypes: ArrayList<String> = ArrayList()

    lateinit var airTimeResponse : GetAirTimeUseCasesResponse

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(
            AirTimeViewModel::class.java
        )
        mDataBinding.apply {
        }

        (activity as AirTimeActivity).setHeaderVisibility(true)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(false)

        mActivityViewModel.popBackStackTo = R.id.airTimeTypeFragment

        airTimeResponse = mActivityViewModel.mAirTimeUseCaseResponse.get()!!

        mAirTimePlansTypes.apply {
            if(mActivityViewModel.isRechargeMobileUseCase.get()!!){
                if(airTimeResponse.rechargeMobile.planList.isNotEmpty()){
                    for(index in airTimeResponse.rechargeMobile.planList.indices){
                           mAirTimePlansTypes.add(airTimeResponse.rechargeMobile.planList[index].plan)
                    }
                }
            }
        }

        mAirTimePlansItemTypeAdapter = CustomizeIconsAdapter(mAirTimePlansTypes,
            object : CustomizeIconsAdapter.CustomizeItemClickListner {
                override fun onCustomizeItemTypeClick(paymentItems: String) {
                    mActivityViewModel.airTimePlanSelected.set(paymentItems)
                    (activity as AirTimeActivity).navController.navigate(R.id.action_airTimePlanFragment_to_airTimeAmountFragment)
                }

            })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mAirTimePlansItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as AirTimeActivity)
        }

        setStrings()
        subscribeObserver()

    }

    private fun setStrings() {
        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("PlanType")
        (activity as AirTimeActivity).setHeaderTitle(
            mActivityViewModel.airTimeSelected.get()!!
        )
    }

    private fun subscribeObserver() {
    }

}