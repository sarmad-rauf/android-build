package com.es.marocapp.usecase.airtime

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.CustomizeIconsAdapter
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.GetAirTimeUseCasesResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.utils.DialogUtils

class AirTimeTypeFragment : BaseFragment<FragmentBillPaymentTypeBinding>() {

    lateinit var mActivityViewModel: AirTimeViewModel
    private lateinit var mAirTimeItemTypeAdapter: PaymentItemsAdapter
    private var mAirTimeTypes: ArrayList<String> = ArrayList()
    private var mAitTimeIcon: ArrayList<Int>  = ArrayList()


    private lateinit var mAirTimeUseCaseResponse : GetAirTimeUseCasesResponse

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(
            AirTimeViewModel::class.java
        )
        mDataBinding.apply {
        }

        mDataBinding.tvPaymentType.text = activity!!.resources.getString(R.string.payment_type)
        (activity as AirTimeActivity).setHeaderTitle(
            (activity as AirTimeActivity).resources.getString(
                R.string.air_time
            )
        )

        (activity as AirTimeActivity).setHeaderVisibility(true)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(false)

        mActivityViewModel.popBackStackTo = -1

        mActivityViewModel.requestForAirTimeUseCasesApi(activity)

        mAirTimeItemTypeAdapter = PaymentItemsAdapter(mAirTimeTypes,mAitTimeIcon,
            object : PaymentItemsAdapter.PaymentItemTypeClickListner {
                override fun onPaymentItemTypeClick(paymentItems: String) {
                    when (paymentItems) {
                        mAirTimeUseCaseResponse.rechargeFixe.titleName -> {
                            mActivityViewModel.isRechargeFixeUseCase.set(true)
                            mActivityViewModel.isRechargeMobileUseCase.set(false)
                            mActivityViewModel.airTimeSelected.set(mAirTimeUseCaseResponse.rechargeFixe.titleName)
                            (activity as AirTimeActivity).navController.navigate(R.id.action_airTimeTypeFragment_to_airTimeAmountFragment)
                        }
                        mAirTimeUseCaseResponse.rechargeMobile.titleName -> {
                            mActivityViewModel.isRechargeFixeUseCase.set(false)
                            mActivityViewModel.isRechargeMobileUseCase.set(true)
                            mActivityViewModel.airTimeSelected.set(mAirTimeUseCaseResponse.rechargeMobile.titleName)
                            (activity as AirTimeActivity).navController.navigate(R.id.action_airTimeTypeFragment_to_airTimePlanFragment)
                        }
                        else -> Toast.makeText(activity, "Nothing Clicked", Toast.LENGTH_SHORT)
                            .show()

                    }
                }

            })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mAirTimeItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as AirTimeActivity)
        }

        setStrings()
        subscribeObserver()
    }

    private fun setStrings() {
        mDataBinding.tvPaymentType.text =LanguageData.getStringValue("PaymentType").toString()
        (activity as AirTimeActivity).setHeaderTitle(
            LanguageData.getStringValue("AirTime").toString()
        )


    }

    private fun subscribeObserver() {
        mActivityViewModel.getAirTimeUseCasesResponseListner.observe(this@AirTimeTypeFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {

                    mAirTimeTypes.clear()
                    mAitTimeIcon.clear()

                    if (!it.rechargeFixe.planList.isNullOrEmpty()) {
                        mAirTimeTypes.add(it.rechargeFixe.titleName)
                        mAitTimeIcon.add(R.drawable.mobile_fix)
                    }

                    if (!it.rechargeMobile.planList.isNullOrEmpty()) {
                        mAirTimeTypes.add(it.rechargeMobile.titleName)
                        mAitTimeIcon.add(R.drawable.mobile)
                    }

                    mAirTimeItemTypeAdapter.notifyDataSetChanged()

                    mAirTimeUseCaseResponse = it
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

}