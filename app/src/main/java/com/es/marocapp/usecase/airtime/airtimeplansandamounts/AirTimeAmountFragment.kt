package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.CustomizeIconsAdapter
import com.es.marocapp.adapter.FirstLetterIconItemAdapter
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.GetAirTimeUseCasesResponse
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.airtime.AirTimeViewModel


class AirTimeAmountFragment : BaseFragment<FragmentBillPaymentTypeBinding>() {

    lateinit var mActivityViewModel: AirTimeViewModel
    private lateinit var mAirTimeAmountsItemTypeAdapter: FirstLetterIconItemAdapter
    private var mAirTimeAmountsTypes: ArrayList<String> = ArrayList()
    private var mAitTimeIcon: ArrayList<Int>  = ArrayList()

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

        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("Amounts")

        (activity as AirTimeActivity).setHeaderVisibility(true)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(false)

        if(mActivityViewModel.isRechargeMobileUseCase.get()!!){
            mActivityViewModel.popBackStackTo = R.id.airTimePlanFragment
            (activity as AirTimeActivity).setHeaderTitle(
                mActivityViewModel.airTimePlanSelected.get()!!
            )
        }

        if(mActivityViewModel.isRechargeFixeUseCase.get()!!){
            mActivityViewModel.popBackStackTo = R.id.airTimeTypeFragment
            (activity as AirTimeActivity).setHeaderTitle(
                mActivityViewModel.airTimeSelected.get()!!
            )
        }

        airTimeResponse = mActivityViewModel.mAirTimeUseCaseResponse.get()!!

        mAirTimeAmountsTypes.apply {
            if(mActivityViewModel.isRechargeMobileUseCase.get()!!){
                mAirTimeAmountsTypes.clear()
                if(airTimeResponse.rechargeMobile.planList.isNotEmpty()){
                    for(index in airTimeResponse.rechargeMobile.planList.indices){
                        if(airTimeResponse.rechargeMobile.planList[index].plan.equals(mActivityViewModel.airTimePlanSelected.get()!!)){
                            mActivityViewModel.airTimeSelectedPlanCodeSelected.set(airTimeResponse.rechargeMobile.planList[index].code.toString())
                            for(amountIndex in airTimeResponse.rechargeMobile.planList[index].amounts.indices){

                                var airTimeAmount = airTimeResponse.rechargeMobile.planList[index].amounts[amountIndex].substringBefore(" ")
                                airTimeAmount = airTimeAmount.removeSuffix("DH").trim()
                                add(airTimeAmount.trim())
                            }
                        }
                    }
                }
            }

            if(mActivityViewModel.isRechargeFixeUseCase.get()!!){
                mAirTimeAmountsTypes.clear()
                if(airTimeResponse.rechargeFixe.planList.isNotEmpty()){
                    for(index in airTimeResponse.rechargeFixe.planList.indices){
                        var airTimeAmount = airTimeResponse.rechargeFixe.planList[index].removeSuffix("DH")
                        mAirTimeAmountsTypes.add(airTimeAmount.trim())
                    }
                }
            }
        }

        mAirTimeAmountsItemTypeAdapter = FirstLetterIconItemAdapter(mAirTimeAmountsTypes,
            object : FirstLetterIconItemAdapter.PaymentItemTypeClickListner {
                override fun onPaymentItemTypeClick(paymentItems: String) {
                    mActivityViewModel.airTimeAmountSelected.set(paymentItems)
                    (activity as AirTimeActivity).navController.navigate(R.id.action_airTimeAmountFragment_to_airTimeMsisdnFragment)
                }

            })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mAirTimeAmountsItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as AirTimeActivity)
        }

        subscribeObserver()

    }

    private fun subscribeObserver() {
    }

}